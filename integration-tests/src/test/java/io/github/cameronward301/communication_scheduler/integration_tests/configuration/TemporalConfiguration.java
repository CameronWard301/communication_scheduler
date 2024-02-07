package io.github.cameronward301.communication_scheduler.integration_tests.configuration;

import io.github.cameronward301.communication_scheduler.integration_tests.properties.IntegrationTestTemporalProperties;
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.codec.CryptographyCodec;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.common.converter.CodecDataConverter;
import io.temporal.common.converter.DefaultDataConverter;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class TemporalConfiguration {
    private final IntegrationTestTemporalProperties integrationTestTemporalProperties;


    @Bean
    public WorkflowServiceStubs workflowServiceStubs() {
        return WorkflowServiceStubs.newServiceStubs(
                WorkflowServiceStubsOptions.newBuilder().setTarget(integrationTestTemporalProperties.getHost()).build()
        );
    }

    @Bean
    public WorkflowClient workflowClient(WorkflowServiceStubs service, CryptographyCodec cryptographyCodec) {
        return WorkflowClient.newInstance(service, WorkflowClientOptions.newBuilder()
                .setNamespace(integrationTestTemporalProperties.getNamespace())
                .setDataConverter(new CodecDataConverter(
                        DefaultDataConverter.newDefaultInstance(),
                        Collections.singletonList(cryptographyCodec)
                ))
                .build());
    }
}
