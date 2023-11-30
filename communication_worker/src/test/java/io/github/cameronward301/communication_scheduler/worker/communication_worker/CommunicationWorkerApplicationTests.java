package io.github.cameronward301.communication_scheduler.worker.communication_worker;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.github.cameronward301.communication_scheduler.worker.communication_worker.properties.WorkerAwsProperties;
import io.github.cameronward301.communication_scheduler.worker.communication_worker.properties.WorkerTemporalProperties;
import io.github.cameronward301.communication_scheduler.worker.communication_worker.worker.CommunicationWorker;
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.CommunicationWorkflowImpl;
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.activities.GetGatewayFromDbActivityImpl;
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.activities.GetPreferencesActivityImpl;
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.activities.SendMessageToGatewayActivityImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClientBuilder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
class CommunicationWorkerApplicationTests {

    @Test
    void contextLoads() {
    }



}
