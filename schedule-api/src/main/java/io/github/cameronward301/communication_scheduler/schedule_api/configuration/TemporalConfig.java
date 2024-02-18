package io.github.cameronward301.communication_scheduler.schedule_api.configuration;

import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.codec.CryptographyCodec;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.client.schedules.ScheduleClient;
import io.temporal.common.converter.CodecDataConverter;
import io.temporal.common.converter.DefaultDataConverter;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

/**
 * Creates temporal clients for use in service layer
 */
@Configuration
@RequiredArgsConstructor
public class TemporalConfig {

    private final CryptographyCodec cryptographyCodec;

    @Value("${temporal-properties.namespace}")
    private String namespace;

    @Bean
    WorkflowServiceStubs workflowServiceStubs(@Value("${temporal-properties.endpoint}") String endpoint) {
        return WorkflowServiceStubs.newServiceStubs(WorkflowServiceStubsOptions.newBuilder()
                        .setTarget(endpoint)
                .build());
    }

    @Bean
    WorkflowClient workflowClient(WorkflowServiceStubs workflowServiceStubs) {
        return WorkflowClient.newInstance(workflowServiceStubs, WorkflowClientOptions.newBuilder()
                .setDataConverter(new CodecDataConverter(
                        DefaultDataConverter.newDefaultInstance(),
                        Collections.singletonList(cryptographyCodec)
                ))
                .setNamespace(namespace)
                .build());
    }

    @Bean
    ScheduleClient scheduleClient(WorkflowServiceStubs workflowServiceStubs) {
        return ScheduleClient.newInstance(workflowServiceStubs);
    }

}
