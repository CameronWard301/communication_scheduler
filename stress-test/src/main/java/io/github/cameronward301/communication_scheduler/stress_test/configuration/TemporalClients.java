package io.github.cameronward301.communication_scheduler.stress_test.configuration;

import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.codec.CryptographyCodec;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.common.converter.CodecDataConverter;
import io.temporal.common.converter.DefaultDataConverter;
import io.temporal.serviceclient.WorkflowServiceStubs;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class TemporalClients {
    private final CryptographyCodec cryptographyCodec;

    @Bean
    WorkflowServiceStubs workflowServiceStubs(){
        return WorkflowServiceStubs.newLocalServiceStubs();
    }

    @Bean
    WorkflowClient workflowClient(WorkflowServiceStubs workflowServiceStubs) {
        return WorkflowClient.newInstance(workflowServiceStubs, WorkflowClientOptions.newBuilder()
                .setDataConverter(new CodecDataConverter(
                        DefaultDataConverter.newDefaultInstance(),
                        Collections.singletonList(cryptographyCodec)
                ))
                .setNamespace("default")
                .build());
    }
}
