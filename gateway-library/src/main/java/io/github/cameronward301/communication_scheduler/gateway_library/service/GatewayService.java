package io.github.cameronward301.communication_scheduler.gateway_library.service;

import io.github.cameronward301.communication_scheduler.gateway_library.model.Content;
import io.github.cameronward301.communication_scheduler.gateway_library.model.GatewayResponse;
import io.github.cameronward301.communication_scheduler.gateway_library.properties.GatewayProperties;
import org.springframework.http.ResponseEntity;

/**
 * Service for sending communications
 *
 * @param <TUser>    the type for the user object
 * @param <TContent> the type for the content object
 */
public interface GatewayService<TUser, TContent extends Content> {
    /**
     * Sends a communication to a user
     *
     * @param gatewayProperties containing all methods needed to get the user details, content and where to send the message to
     * @return gatewayResponse containing the message hash and user id or an error message if there was an error
     */
    ResponseEntity<GatewayResponse> sendCommunication(GatewayProperties<TUser, TContent> gatewayProperties);
}
