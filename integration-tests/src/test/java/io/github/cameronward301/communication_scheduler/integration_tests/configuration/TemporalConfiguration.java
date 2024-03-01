package io.github.cameronward301.communication_scheduler.integration_tests.configuration;

import io.github.cameronward301.communication_scheduler.integration_tests.model.schedule.ScheduleEntity;
import io.github.cameronward301.communication_scheduler.integration_tests.properties.IntegrationTestTemporalProperties;
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.CommunicationWorkflow;
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.codec.CryptographyCodec;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.schedules.*;
import io.temporal.common.SearchAttributeKey;
import io.temporal.common.SearchAttributes;
import io.temporal.common.converter.CodecDataConverter;
import io.temporal.common.converter.DefaultDataConverter;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.*;

import static java.lang.String.format;

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

    @Bean
    public ScheduleClient scheduleClient(WorkflowServiceStubs workflowServiceStubs) {
        return ScheduleClient.newInstance(workflowServiceStubs);
    }

    @Bean
    public List<ScheduleEntity> scheduleEntities() {
        List<ScheduleEntity> scheduleEntities = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            String userId;
            String gatewayId;
            String scheduleId = "integration-test-" + UUID.randomUUID();
            if (i < 1) {
                userId = "user1";
            } else {
                userId = "user2";
            }

            if (i < 1) {
                gatewayId = "gateway1";
            } else if (i < 2) {
                gatewayId = "gateway2";
            } else {
                gatewayId = "gateway3";
            }
            scheduleEntities.add(ScheduleEntity.builder()
                    .scheduleId(scheduleId)
                    .schedule(Schedule.newBuilder()
                            .setState(ScheduleState.newBuilder()
                                    .setPaused(false)
                                    .build())
                            .setSpec(ScheduleSpec.newBuilder()
                                    .setIntervals(List.of(new ScheduleIntervalSpec(Duration.parse("PT2H"), Duration.ZERO)))
                                    .build())
                            .setAction(ScheduleActionStartWorkflow.newBuilder()
                                    .setWorkflowType(CommunicationWorkflow.class)
                                    .setArguments(Map.of(
                                            "userId", userId,
                                            "gatewayId", gatewayId
                                    ))
                                    .setOptions(WorkflowOptions.newBuilder()
                                            .setTaskQueue("test-queue")
                                            .setWorkflowExecutionTimeout(Duration.ofSeconds(1))
                                            .setTypedSearchAttributes(getSearchAttributes(userId, gatewayId, scheduleId))
                                            .setWorkflowId(
                                                    format("%s:%s:%s:",
                                                            gatewayId,
                                                            userId,
                                                            scheduleId))
                                            .build())
                                    .build())
                            .build())
                    .scheduleOptions(ScheduleOptions.newBuilder()
                            .setTypedSearchAttributes(getSearchAttributes(userId, gatewayId, scheduleId))
                            .build())
                    .build());
        }
        return scheduleEntities;
    }

    @Bean
    public ScheduleEntity scheduleEntity() {
        String userId = "user1";
        String gatewayId = "gateway1";
        String scheduleId = "integration-test-" + UUID.randomUUID();
        return ScheduleEntity.builder()
                .scheduleId(scheduleId)
                .schedule(Schedule.newBuilder()
                        .setState(ScheduleState.newBuilder()
                                .setPaused(false)
                                .build())
                        .setSpec(ScheduleSpec.newBuilder()
                                .setIntervals(List.of(new ScheduleIntervalSpec(Duration.parse("PT2H"), Duration.ZERO)))
                                .build())
                        .setAction(ScheduleActionStartWorkflow.newBuilder()
                                .setWorkflowType(CommunicationWorkflow.class)
                                .setArguments(Map.of(
                                        "userId", userId,
                                        "gatewayId", gatewayId
                                ))
                                .setOptions(WorkflowOptions.newBuilder()
                                        .setTaskQueue("test-queue")
                                        .setTypedSearchAttributes(getSearchAttributes(userId, gatewayId, scheduleId))
                                        .setWorkflowId(
                                                format("%s:%s:%s:",
                                                        gatewayId,
                                                        userId,
                                                        scheduleId))
                                        .build())
                                .build())
                        .build())
                .scheduleOptions(ScheduleOptions.newBuilder()
                        .setTypedSearchAttributes(getSearchAttributes(userId, gatewayId, scheduleId))
                        .build())
                .build();
    }

    private SearchAttributes getSearchAttributes(String userId, String gatewayId, String scheduleId) {
        return SearchAttributes.newBuilder()
                .set(SearchAttributeKey.forKeyword("userId"), userId)
                .set(SearchAttributeKey.forKeyword("gatewayId"), gatewayId)
                .set(SearchAttributeKey.forKeyword("scheduleId"), scheduleId)
                .build();
    }
}
