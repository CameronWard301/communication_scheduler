package com.github.cameronward301.communication_scheduler.workflows.communication_workflow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cameronward301.communication_scheduler.workflows.communication_workflow.activities.GetGatewayFromDbActivity;
import com.github.cameronward301.communication_scheduler.workflows.communication_workflow.activities.GetPreferencesActivity;
import com.github.cameronward301.communication_scheduler.workflows.communication_workflow.activities.SendMessageToGatewayActivity;
import com.github.cameronward301.communication_scheduler.workflows.communication_workflow.model.Preferences;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.util.Map;

/**
 * Communication Workflow Implementation
 */
@Slf4j
public class CommunicationWorkflowImpl implements CommunicationWorkflow {

    ObjectMapper objectMapper = new ObjectMapper();
    @Value("${temporal-properties.task-queue}")
    private String taskQueue;
    private final GetPreferencesActivity getSettingsActivity = Workflow.newActivityStub(GetPreferencesActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(5))
                    .setRetryOptions(RetryOptions.newBuilder()
                            .setMaximumInterval(Duration.ofSeconds(5)).build())
                    .setTaskQueue(taskQueue)
                    .build());

    @Override
    public String sendCommunication(Map<String, String> payload) throws JsonProcessingException {
        log.info("Started workflow with payload: {}", payload.toString());
        log.info("Getting preferences");
        Preferences preferences = getSettingsActivity.getPreferences();
        log.info("Got preferences");

        log.info("Setting up activity retry policies and timeouts");
        GetGatewayFromDbActivity getGatewayFromDbActivity = Workflow.newActivityStub(GetGatewayFromDbActivity.class,
                ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(preferences.getStartToCloseTimeout())
                        .setRetryOptions(RetryOptions.newBuilder()
                                .setMaximumInterval(preferences.getMaximumInterval())
                                .setInitialInterval(preferences.getInitialInterval())
                                .setBackoffCoefficient(preferences.getBackoffCoefficient())
                                .setMaximumAttempts(preferences.getMaximumAttempts())
                                .build())
                        .build()
        );

        SendMessageToGatewayActivity sendMessageToGatewayActivity = Workflow.newActivityStub(SendMessageToGatewayActivity.class,
                ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(preferences.getStartToCloseTimeout())
                        .setRetryOptions(RetryOptions.newBuilder()
                                .setMaximumInterval(preferences.getMaximumInterval())
                                .setInitialInterval(preferences.getInitialInterval())
                                .setBackoffCoefficient(preferences.getBackoffCoefficient())
                                .setMaximumAttempts(preferences.getMaximumAttempts())
                                .build())
                        .build()
        );
        log.info("Completed set up of activity retry policies and timeouts");


        log.info("Getting gateway URL");
        String gatewayURL = getGatewayFromDbActivity.getGatewayEndpointUrl(payload.get("gatewayId"));
        log.info("Got gateway URL: {}", gatewayURL);

        log.info("Sending message to gateway");
        String response = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(sendMessageToGatewayActivity.invokeGateway(payload.get("userId"), Workflow.getInfo().getRunId(), gatewayURL));
        log.info("Got response from gateway: {}", response);
        
        return response;

    }
}
