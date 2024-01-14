package io.github.cameronward301.communication_scheduler.workflows.communication_workflow.exception;

/**
 * Exception thrown when a gateway is not found by its id.
 */
public class GatewayNotFoundException extends RuntimeException {
    private static final String MESSAGE = "Gateway with id %s not found";

    public GatewayNotFoundException(String gatewayId) {
        super(String.format(MESSAGE, gatewayId));
    }
}
