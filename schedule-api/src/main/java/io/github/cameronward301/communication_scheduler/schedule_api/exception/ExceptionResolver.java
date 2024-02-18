package io.github.cameronward301.communication_scheduler.schedule_api.exception;

import io.grpc.StatusRuntimeException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Exception resolver for the schedule API
 */
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExceptionResolver {

    /**
     * Handles exceptions thrown by the schedule API
     *
     * @param exception The RequestException thrown by the schedule API
     * @return A response entity containing the exception message and HTTP status code
     */
    @ExceptionHandler(RequestException.class)
    protected ResponseEntity<Object> handleRequestException(RequestException exception) {
        return new ResponseEntity<>(exception.getMessage(), exception.getHttpStatus());
    }

    /**
     * Handles runtime exceptions thrown by the Temporal Schedule client
     *
     * @param exception The StatusRuntimeException thrown by the schedule client
     * @return A response entity containing the message and BAD request (400) HTTP status code
     */
    @ExceptionHandler(StatusRuntimeException.class)
    protected ResponseEntity<Object> handleStatusRuntimeException(StatusRuntimeException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
