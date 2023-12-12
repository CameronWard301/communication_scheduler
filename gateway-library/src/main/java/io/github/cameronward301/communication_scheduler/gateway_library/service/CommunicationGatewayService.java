package io.github.cameronward301.communication_scheduler.gateway_library.service;

import io.github.cameronward301.communication_scheduler.gateway_library.exception.ContentDeliveryException;
import io.github.cameronward301.communication_scheduler.gateway_library.helper.HashHelper;
import io.github.cameronward301.communication_scheduler.gateway_library.model.CommunicationHistory;
import io.github.cameronward301.communication_scheduler.gateway_library.model.Content;
import io.github.cameronward301.communication_scheduler.gateway_library.model.GatewayResponse;
import io.github.cameronward301.communication_scheduler.gateway_library.properties.GatewayProperties;
import io.github.cameronward301.communication_scheduler.gateway_library.user.content.UserAndContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Service for sending communications. Should be instantiated as a bean in the gateway application
 *
 * @param <TUser>    the type for the user object
 * @param <TContent> the type for the content object
 */
@Slf4j
public class CommunicationGatewayService<TUser, TContent extends Content> implements GatewayService<TUser, TContent> {
    @Override
    public ResponseEntity<GatewayResponse> sendCommunication(GatewayProperties<TUser, TContent> gatewayProperties) {
        String messageHash;
        GatewayResponse gatewayResponse = GatewayResponse.builder().build();
        try {
            UserAndContent<TUser, TContent> userAndContent = gatewayProperties.getUserContentService().getUserAndContent(gatewayProperties.getUserId());
            messageHash = HashHelper.messageHash(gatewayProperties.getWorkflowRunId(), userAndContent.getContent().getContentString());
            CommunicationHistory previousCommunication = gatewayProperties.getCommunicationHistoryAccessProvider().getPreviousCommunicationByMessageHash(messageHash);

            gatewayResponse.setMessageHash(messageHash);
            gatewayResponse.setUserId(gatewayProperties.getUserId());

            if (previousCommunication.isPreviousMessageSent()) {
                return new ResponseEntity<>(gatewayResponse, HttpStatus.OK);
            }

            try {
                gatewayProperties.getCommunicationHistoryAccessProvider().storeCommunication(gatewayProperties.getWorkflowRunId(), gatewayProperties.getUserId(), messageHash);
                gatewayProperties.getContentDeliveryService().sendContent(userAndContent.getUser(), userAndContent.getContent());
            } catch (ContentDeliveryException e) {
                log.error("Error sending communication", e);
                log.info("Removing communication history for message hash: " + messageHash);
                gatewayProperties.getCommunicationHistoryAccessProvider().removeCommunicationHistoryByMessageHash(messageHash);
                gatewayResponse.setErrorMessage(e.getMessage());
                return new ResponseEntity<>(gatewayResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return new ResponseEntity<>(gatewayResponse, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error sending communication", e);
            gatewayResponse.setErrorMessage(e.getMessage());
            return new ResponseEntity<>(gatewayResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
