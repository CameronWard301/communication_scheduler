package com.github.cameronward301.communication_scheduler.workflows.communication_workflow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.cameronward301.communication_scheduler.workflows.communication_workflow.activities.GetPreferencesActivity;
import com.github.cameronward301.communication_scheduler.workflows.communication_workflow.model.Preferences;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.util.Map;


public class CommunicationWorkflowImpl implements CommunicationWorkflow{

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
        Preferences preferences = getSettingsActivity.getPreferences();

        /*GetGatewayFromDbActivity getGatewayFromDbActivity = Workflow.newActivityStub(GetGatewayFromDbActivity.class,
                preferences.getActivityOptions());

        SendMessageToGatewayActivity sendMessageToGatewayActivity = Workflow.newActivityStub(SendMessageToGatewayActivity.class,
                preferences.getActivityOptions());

        String gatewayURL = getGatewayFromDbActivity.getGatewayEndpointUrl(payload.get("gatewayId"));

        return sendMessageToGatewayActivity.invokeGateway(payload.get("userId"), Workflow.getInfo().getRunId(), gatewayURL).toString();*/
        return "workflow-complete";
    }
}
