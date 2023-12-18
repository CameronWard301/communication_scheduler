package io.github.cameronward301.communication_scheduler.gateway_library.controller;

import io.github.cameronward301.communication_scheduler.gateway_library.model.Content;
import io.github.cameronward301.communication_scheduler.gateway_library.model.GatewayRequest;
import io.github.cameronward301.communication_scheduler.gateway_library.model.GatewayResponse;
import io.github.cameronward301.communication_scheduler.gateway_library.properties.GatewayProperties;
import io.github.cameronward301.communication_scheduler.gateway_library.service.CommunicationGatewayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Interface for the gateway controller
 *
 * @see <a href="https://app.swaggerhub.com/apis/CameronWard301/Gateway_API/1.1.1">API Sepcification</a>
 * for more information on request and return requirements
 * @see <a href="https://github.com/CameronWard301/communication_scheduler/tree/main/email-gateway">Email Gateway</a>
 * for an example implementation
 */
public interface GatewayController<TUser, TContent extends Content> {


    /**
     * Sends a communication to a user using the provided gatewayProperties and communicationGatewayService
     *
     * @param gatewayRequest              the request containing the userId and workflowRunId
     * @param gatewayProperties           the properties for the gateway
     * @param communicationGatewayService the service for sending the communication
     * @param <TUser>                     the type for the user object
     * @param <TContent>                  the type for the content object
     * @return a GatewayResponse JSON object containing the userId and messageHash
     */
    static <TUser, TContent extends Content> ResponseEntity<GatewayResponse> sendCommunication(GatewayRequest gatewayRequest,
                                                                                               GatewayProperties<TUser, TContent> gatewayProperties,
                                                                                               CommunicationGatewayService<TUser, TContent> communicationGatewayService) {

        gatewayProperties.setUserId(gatewayRequest.userId());
        gatewayProperties.setWorkflowRunId(gatewayRequest.workflowRunId());
        return communicationGatewayService.sendCommunication(gatewayProperties);
    }

    /**
     * Sends a communication to a user, call GatewayController.sendCommunication to send the communication to the user
     *
     * @param gatewayRequest a GatewayRequest JSON object containing the userId and workflowRunId
     * @return a GatewayResponse JSON object containing the userId and messageHash
     */
    @PostMapping
    ResponseEntity<GatewayResponse> processGatewayRequest(@RequestBody GatewayRequest gatewayRequest);
}
