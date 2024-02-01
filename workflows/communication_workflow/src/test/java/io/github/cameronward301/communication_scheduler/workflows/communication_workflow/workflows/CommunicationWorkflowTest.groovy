package io.github.cameronward301.communication_scheduler.workflows.communication_workflow.workflows

import io.fabric8.kubernetes.api.model.ConfigMapBuilder
import io.fabric8.kubernetes.client.KubernetesClient
import io.fabric8.kubernetes.client.dsl.MixedOperation
import io.fabric8.kubernetes.client.dsl.Resource
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.CommunicationWorkflow
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.CommunicationWorkflowImpl
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.activities.GetGatewayFromDbActivityImpl
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.activities.GetPreferencesActivityImpl
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.activities.SendMessageToGatewayActivityImpl
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.model.Gateway
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.model.Preferences
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.properties.TemporalProperties
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.repository.GatewayRepository
import io.temporal.api.enums.v1.IndexedValueType
import io.temporal.client.WorkflowClient
import io.temporal.client.WorkflowOptions
import io.temporal.testing.TestWorkflowEnvironment
import io.temporal.worker.Worker
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.springframework.web.reactive.function.client.WebClient
import spock.lang.Specification

class CommunicationWorkflowTest extends Specification {

    private TestWorkflowEnvironment testWorkflowEnvironment
    private Worker worker
    private WorkflowClient workflowClient
    private TemporalProperties temporalProperties
    private KubernetesClient kubernetesClient
    GatewayRepository gatewayRepository = Mock(GatewayRepository.class)

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
    final String API_KEY = "1234"


    def setup() {
        testWorkflowEnvironment = TestWorkflowEnvironment.newInstance()
        worker = testWorkflowEnvironment.newWorker(TASK_QUEUE)
        testWorkflowEnvironment.registerSearchAttribute("gatewayId", IndexedValueType.INDEXED_VALUE_TYPE_TEXT)
        testWorkflowEnvironment.registerSearchAttribute("userId", IndexedValueType.INDEXED_VALUE_TYPE_TEXT)
        testWorkflowEnvironment.registerSearchAttribute("scheduleId", IndexedValueType.INDEXED_VALUE_TYPE_TEXT)

        worker.registerWorkflowImplementationTypes(CommunicationWorkflowImpl.class)
        workflowClient = testWorkflowEnvironment.getWorkflowClient()

        TemporalProperties properties = new TemporalProperties()
        properties.setNamespace(NAMESPACE)
        temporalProperties = properties

        this.kubernetesClient = Mock(KubernetesClient.class)

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


        gatewayRepository.findById(GATEWAY_ID) >> Optional.of(Gateway.builder().endpointUrl(gatewayUrl).build())


        worker.registerActivitiesImplementations(new GetPreferencesActivityImpl(temporalProperties, kubernetesClient))
        worker.registerActivitiesImplementations(new GetGatewayFromDbActivityImpl(gatewayRepository))
        worker.registerActivitiesImplementations(new SendMessageToGatewayActivityImpl(webClient, API_KEY))

        final String messageHash = "test-hash"
        final String USER_ID = "test-user"
        final String SCHEDULE_ID = "mySchedule"
        String responseJSON = "{\"userId\":\"" + USER_ID + "\",\"messageHash\":\"" + messageHash + "\"}"
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json").setBody(responseJSON))
        testWorkflowEnvironment.start()

        CommunicationWorkflow communicationWorkflow = workflowClient.newWorkflowStub(CommunicationWorkflow.class, WorkflowOptions.newBuilder().setTaskQueue(TASK_QUEUE).setWorkflowId(GATEWAY_ID + ":" + USER_ID + ":" + SCHEDULE_ID + ":-timestamp").build())

        when: "communication workflow is started"
        Map<String, String> workflowResult = communicationWorkflow.sendCommunication(Map.of("gatewayId", GATEWAY_ID, "userId", USER_ID))

        then: "workflow completes successfully"
        workflowResult.get("status") == "complete"
        workflowResult.get("userId") == USER_ID
        workflowResult.get("messageHash") == messageHash
    }
}
