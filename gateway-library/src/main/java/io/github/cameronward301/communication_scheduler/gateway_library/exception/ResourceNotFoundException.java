package io.github.cameronward301.communication_scheduler.gateway_library.exception;

/**
 * Exception thrown if an error occurs finding a user or user content in the database
 */
public class ResourceNotFoundException extends RuntimeException {
    /**
     * Thrown when there is an error retrieving the user or content from an external database
     *
     * @param message to be returned with the exception
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
