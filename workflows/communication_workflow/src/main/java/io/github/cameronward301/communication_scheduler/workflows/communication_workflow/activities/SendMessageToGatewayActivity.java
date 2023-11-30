package io.github.cameronward301.communication_scheduler.workflows.communication_workflow.activities;

import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.model.Preferences;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

import java.util.Map;

/**
 * Activity interface for sending a message to the gateway
 */
@ActivityInterface
public interface SendMessageToGatewayActivity {

    /**
     * Trigger a gateway to send a communication to a specific customer
     *
     * @param userId        The id of the user to send the communication to
     * @param workflowRunId The id of the current workflow run triggering the communication
     * @param gatewayUrl    The url of the gateway to send the communication to
     * @param preferences   The preferences for the activity
     * @return The response from the gateway containing the status code
     */
    @ActivityMethod
    Map<String, String> invokeGateway(String userId, String workflowRunId, String gatewayUrl, Preferences preferences);
}
