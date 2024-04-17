package io.github.cameronward301.communication_scheduler.gateway_library.model;

/**
 * Any gateway controller should accept this as the request body
 * @param userId the userId of the user to get the content for
 * @param workflowRunId the workflow run id of the workflow run sending the communication
 */
public record GatewayRequest(String userId, String workflowRunId) {
}
