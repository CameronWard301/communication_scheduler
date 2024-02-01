package io.github.cameronward301.communication_scheduler.preferences_api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a request is invalid
 */
@Getter
public class RequestException extends RuntimeException {
    private final HttpStatus httpStatus;

    /**
     * Constructor for RequestException
     *
     * @param message    The exception message
     * @param httpStatus The HTTP status code
     */
    public RequestException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
