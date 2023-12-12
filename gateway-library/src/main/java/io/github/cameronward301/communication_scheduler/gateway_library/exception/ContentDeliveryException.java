package io.github.cameronward301.communication_scheduler.gateway_library.exception;

import lombok.Getter;

/**
 * Exception thrown when there is an error delivering content to a user via the ContentDeliveryService
 */
@Getter
public class ContentDeliveryException extends RuntimeException {

    /**
     * Thrown when there is an error delivering content to a user via the ContentDeliveryService
     *
     * @param message to be returned with the exception
     */
    public ContentDeliveryException(String message) {
        super(message);
    }
}
