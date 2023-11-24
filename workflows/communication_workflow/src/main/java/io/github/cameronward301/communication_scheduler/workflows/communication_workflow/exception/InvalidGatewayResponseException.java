package io.github.cameronward301.communication_scheduler.workflows.communication_workflow.exception;

/**
 * Invalid Gateway Response Exception thrown if the gateway does not return valid response JSON or is missing the userId or messageHash fields
 */
public class InvalidGatewayResponseException extends RuntimeException {
    private static final String MESSAGE = "Gateway did not return a valid response";

    public InvalidGatewayResponseException() {
        super(MESSAGE);
    }
}
