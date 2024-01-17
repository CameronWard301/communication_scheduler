package io.github.cameronward301.communication_scheduler.gateway_library.exception

import spock.lang.Specification

class ExceptionTest extends Specification {

        def "Should create ContentDeliveryException with message"() {
            given:
            String message = "test-message"

            when:
            def exception = new ContentDeliveryException(message)

            then:
            exception.getMessage() == message
        }


        def "Should create ResourceNotFoundException with message"() {
            given:
            String message = "test-message"

            when:
            def exception = new ResourceNotFoundException(message)

            then:
            exception.getMessage() == message
        }
}
