package io.github.cameronward301.communication_scheduler.history_api.exception;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
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

}
