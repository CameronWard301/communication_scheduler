package io.github.cameronward301.communication_scheduler.gateway_library.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Result sent back to the worker from the gateway
 */
@Builder
@Getter
@Setter
public class GatewayResponse {
    /**
     * The userId of the user that the message was sent to
     */
    private String userId;

    /**
     * The messageHash of the message that was sent
     */
    private String messageHash;

    /**
     * Error message if there was an error sending the message
     */
    private String errorMessage;
}
