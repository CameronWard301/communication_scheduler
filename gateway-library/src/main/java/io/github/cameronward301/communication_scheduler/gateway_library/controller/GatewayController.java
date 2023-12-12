package io.github.cameronward301.communication_scheduler.gateway_library.controller;

import io.github.cameronward301.communication_scheduler.gateway_library.model.GatewayResponse;
import org.springframework.http.ResponseEntity;

/**
 * Interface for the gateway controller
 *
 * @see <a href="https://app.swaggerhub.com/apis/CameronWard301/Gateway_API/1.1.1">API Sepcification</a>
 * for more information on request and return requirements
 * @see <a href="https://github.com/CameronWard301/communication_scheduler/tree/main/email-gateway">Email Gateway</a>
 * for an example implementation
 */
public interface GatewayController {
    /**
     * Sends a communication to a user
     *
     * @param userId        the id of the user to send the communication to
     * @param workflowRunId the id of the workflow run that triggered the communication, used to generate the message hash
     * @return a GatewayResponse JSON object containing the userId and messageHash
     */
    ResponseEntity<GatewayResponse> sendCommunication(String userId, String workflowRunId);
}
