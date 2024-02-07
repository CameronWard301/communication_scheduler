package io.github.cameronward301.communication_scheduler.worker.communication_worker.worker;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.github.cameronward301.communication_scheduler.worker.communication_worker.properties.WorkerTemporalProperties;
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.CommunicationWorkflowImpl;
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.activities.GetGatewayFromDbActivityImpl;
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.activities.GetPreferencesActivityImpl;
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.activities.SendMessageToGatewayActivityImpl;
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.codec.CryptographyCodec;
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.repository.GatewayRepository;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.common.converter.CodecDataConverter;
import io.temporal.common.converter.DefaultDataConverter;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;

@Component
@Slf4j
public class CommunicationWorker {

    public CommunicationWorker(
            WorkerTemporalProperties temporalProperties,
            GatewayRepository gatewayRepository,
            WebClient webClient,
            @Value("${worker.apiKey}") String GATEWAY_API_KEY,
            CryptographyCodec cryptographyCodec
    ) {
        log.debug("Connecting to temporal at {}", temporalProperties.getEndpoint());
        WorkflowServiceStubs service = WorkflowServiceStubs.newServiceStubs(WorkflowServiceStubsOptions.newBuilder()
                .setTarget(temporalProperties.getEndpoint()).build());

        WorkflowClient client = WorkflowClient.newInstance(service, WorkflowClientOptions.newBuilder()
                .setDataConverter(new CodecDataConverter(
                        DefaultDataConverter.newDefaultInstance(),
                        Collections.singletonList(cryptographyCodec)
                ))
                .setNamespace(temporalProperties.getNamespace())
                .build());

        log.debug("Connected to temporal");

        WorkerFactory factory = WorkerFactory.newInstance(client);
        Worker worker = factory.newWorker(temporalProperties.getTaskQueue());

        log.debug("Connecting to kubernetes");
        KubernetesClient kubernetesClient = new KubernetesClientBuilder().build();
        log.debug("Connected to kubernetes");


        log.debug("Registering workflow and activities");
        worker.registerWorkflowImplementationTypes(CommunicationWorkflowImpl.class);
        worker.registerActivitiesImplementations(new GetPreferencesActivityImpl(temporalProperties, kubernetesClient));
        worker.registerActivitiesImplementations(new GetGatewayFromDbActivityImpl(gatewayRepository));
        worker.registerActivitiesImplementations(new SendMessageToGatewayActivityImpl(webClient, GATEWAY_API_KEY));
        log.debug("Registered workflow and activities");

        log.info("Worker started");
        factory.start();
    }
}
