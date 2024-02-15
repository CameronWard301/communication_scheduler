package io.github.cameronward301.communication_scheduler.schedule_api.exception

import io.grpc.Status
import io.grpc.StatusRuntimeException
import org.springframework.http.HttpStatus
import spock.lang.Specification

class ExceptionResolverTest extends Specification {

    private ExceptionResolver exceptionResolver

    def setup() {
        exceptionResolver = new ExceptionResolver()
    }

    def "handleRequestExceptionMethod"() {
        given: "RequestException"
        def requestException = new RequestException("test-message", HttpStatus.BAD_REQUEST)

        when: "handleRequestException is called"
        def response = exceptionResolver.handleRequestException(requestException)

        then: "response is correct"
        response.getStatusCode() == HttpStatus.BAD_REQUEST
        response.getBody() == "test-message"
    }

    def "handle status runtime exception"() {
        given: "StatusRuntimeException"
        def requestException = new StatusRuntimeException(Status.NOT_FOUND)

        when: "handleRequestException is called"
        def response = exceptionResolver.handleStatusRuntimeException(requestException)

        then: "response is correct"
        response.getStatusCode() == HttpStatus.BAD_REQUEST
        response.getBody() == "NOT_FOUND"
    }


}
