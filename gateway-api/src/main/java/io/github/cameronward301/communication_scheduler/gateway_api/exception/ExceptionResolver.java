package io.github.cameronward301.communication_scheduler.gateway_api.exception;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExceptionResolver {

    @ExceptionHandler(RequestException.class)
    protected ResponseEntity<Object> handleRequestException(RequestException ex) {
        return new ResponseEntity<>(ex.getMessage(), ex.getHttpStatus());
    }

}
