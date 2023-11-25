package io.github.cameronward301.communication_scheduler.worker.communication_worker.worker;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.github.cameronward301.communication_scheduler.worker.communication_worker.properties.WorkerAwsProperties;
import io.github.cameronward301.communication_scheduler.worker.communication_worker.properties.WorkerTemporalProperties;
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.CommunicationWorkflowImpl;
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.activities.GetGatewayFromDbActivityImpl;
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.activities.GetPreferencesActivityImpl;
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.activities.SendMessageToGatewayActivityImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

@Component
@Slf4j
public class CommunicationWorker {
    public CommunicationWorker(WorkerTemporalProperties temporalProperties, WorkerAwsProperties awsProperties) {
        WorkflowServiceStubs service = WorkflowServiceStubs.newServiceStubs(WorkflowServiceStubsOptions.newBuilder()
                .setTarget(temporalProperties.getEndpoint()).build());
        WorkflowClient client = WorkflowClient.newInstance(service, WorkflowClientOptions.newBuilder()
                .setNamespace(temporalProperties.getNamespace())
                .build());

        WorkerFactory factory = WorkerFactory.newInstance(client);
        Worker worker = factory.newWorker(temporalProperties.getTaskQueue());

        DynamoDbAsyncClient dynamoDbAsyncClient = DynamoDbAsyncClient.builder()
                .region(Region.of(awsProperties.getRegion()))
                .build();

        WebClient webClient = WebClient.create();
        KubernetesClient kubernetesClient = new KubernetesClientBuilder().build();


        worker.registerWorkflowImplementationTypes(CommunicationWorkflowImpl.class);
        worker.registerActivitiesImplementations(new GetPreferencesActivityImpl(temporalProperties, kubernetesClient));
        worker.registerActivitiesImplementations(new GetGatewayFromDbActivityImpl(awsProperties, dynamoDbAsyncClient));
        worker.registerActivitiesImplementations(new SendMessageToGatewayActivityImpl(webClient));

        log.info("Worker started");
        factory.start();
    }
}
