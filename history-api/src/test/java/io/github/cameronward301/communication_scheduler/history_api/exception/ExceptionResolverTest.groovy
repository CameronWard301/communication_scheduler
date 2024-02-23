package io.github.cameronward301.communication_scheduler.history_api.exception


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


}
