package io.github.cameronward301.communication_scheduler.workflows.communication_workflow.workflows

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.CommunicationWorkflow
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.activities.GetPreferencesActivityImpl
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.model.Preferences
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.properties.AwsProperties
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.properties.TemporalProperties
import io.fabric8.kubernetes.api.model.ConfigMapBuilder
import io.fabric8.kubernetes.client.KubernetesClient
import io.fabric8.kubernetes.client.dsl.MixedOperation
import io.fabric8.kubernetes.client.dsl.Resource
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.CommunicationWorkflowImpl
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.activities.GetGatewayFromDbActivityImpl
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.activities.SendMessageToGatewayActivityImpl

import io.temporal.client.WorkflowClient
import io.temporal.client.WorkflowOptions
import io.temporal.testing.TestWorkflowEnvironment
import io.temporal.worker.Worker
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.springframework.web.reactive.function.client.WebClient
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.DynamoDbResponse
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse
import spock.lang.Specification

import java.util.concurrent.CompletableFuture

class CommunicationWorkflowTest extends Specification {

    private TestWorkflowEnvironment testWorkflowEnvironment
    private Worker worker
    private WorkflowClient workflowClient
    private TemporalProperties temporalProperties
    private KubernetesClient kubernetesClient
    private AwsProperties awsProperties
    DynamoDbAsyncClient dynamoDbAsyncClient = Mock(DynamoDbAsyncClient.class)

    public static MockWebServer mockWebServer

    //Mock kubernetes client and resources
    def mockConfigMapOperations = Mock(MixedOperation)
    def mockConfigMap = Mock(Resource)

    private WebClient webClient
    private Preferences preferences = new Preferences()
    String gatewayUrl
    final String NAMESPACE = "default"
    final String TASK_QUEUE = "test-task-queue"
    final String GATEWAY_ID = "test-gateway"
    final ObjectMapper objectMapper = new ObjectMapper()


    def setup() {
        testWorkflowEnvironment = TestWorkflowEnvironment.newInstance()
        worker = testWorkflowEnvironment.newWorker(TASK_QUEUE)

        worker.registerWorkflowImplementationTypes(CommunicationWorkflowImpl.class)
        workflowClient = testWorkflowEnvironment.getWorkflowClient()

        TemporalProperties properties = new TemporalProperties()
        properties.setNamespace(NAMESPACE)
        temporalProperties = properties

        this.kubernetesClient = Mock(KubernetesClient.class)

        awsProperties = new AwsProperties()
        awsProperties.setTable_name("test_table")
        awsProperties.setKey_name("id")

        mockWebServer = new MockWebServer()
        mockWebServer.start()
        def baseUrl = String.format("http://localhost:%s", mockWebServer.getPort())
        gatewayUrl = baseUrl + "/test-gateway"

        webClient = WebClient.create()

        preferences.setGatewayTimeoutSeconds(3)
    }

    def cleanup() {
        testWorkflowEnvironment.close()
    }

    def "communication workflow should complete successfully"() {
        given: "worker is registered with the activities and mocked dependencies"
        def configMapData = '{"startToCloseTimeout":"PT10S","maximumAttempts":100,"backoffCoefficient":2.0,"initialInterval":"PT1S","maximumInterval":"PT100S"}'
        def configMap = new ConfigMapBuilder()
                .withNewMetadata().withName("preferences").withNamespace(NAMESPACE).endMetadata()
                .addToData("GatewayTimeoutSeconds", "60")
                .addToData("RetryPolicy", configMapData).build()
        (kubernetesClient.configMaps()) >> mockConfigMapOperations
        (mockConfigMapOperations.inNamespace(NAMESPACE)) >> mockConfigMapOperations
        (mockConfigMapOperations.withName("preferences")) >> mockConfigMap
        (mockConfigMap.get()) >> configMap


        DynamoDbResponse getItemResponse = GetItemResponse.builder()
                .item(Collections.singletonMap("endpoint_url", AttributeValue.builder().s(gatewayUrl).build()))
                .build()

        GetItemRequest getItemRequest = GetItemRequest.builder()
                .tableName(awsProperties.getTable_name())
                .key(Map.of(awsProperties.getKey_name(), AttributeValue.builder().s(GATEWAY_ID).build()))
                .build() as GetItemRequest

        dynamoDbAsyncClient.getItem(getItemRequest) >> CompletableFuture.completedFuture(getItemResponse)


        worker.registerActivitiesImplementations(new GetPreferencesActivityImpl(temporalProperties, kubernetesClient))
        worker.registerActivitiesImplementations(new GetGatewayFromDbActivityImpl(awsProperties, dynamoDbAsyncClient))
        worker.registerActivitiesImplementations(new SendMessageToGatewayActivityImpl(webClient))

        final String messageHash = "test-hash"
        final String USER_ID = "test-user"
        String responseJSON = "{\"userId\":\"" + USER_ID + "\",\"messageHash\":\""+ messageHash + "\"}"
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json").setBody(responseJSON))
        testWorkflowEnvironment.start()

        CommunicationWorkflow communicationWorkflow = workflowClient.newWorkflowStub(CommunicationWorkflow.class, WorkflowOptions.newBuilder().setTaskQueue(TASK_QUEUE).build())

        when: "communication workflow is started"
        String workflowResult = communicationWorkflow.sendCommunication(Map.of("gatewayId", GATEWAY_ID, "userId", USER_ID))

        then: "workflow completes successfully"
        Map<String, String> resultObject = objectMapper.readValue(workflowResult, Map.class)
        resultObject.get("status") == "complete"
        resultObject.get("userId") == USER_ID
        resultObject.get("messageHash") == messageHash
    }
}
