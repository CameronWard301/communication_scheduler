package io.github.cameronward301.communication_scheduler.data_converter_api.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.protobuf.InvalidProtocolBufferException
import io.temporal.api.common.v1.Payload
import spock.lang.Specification

class JsonPayloadServiceTest extends Specification {
    def objectMapper = new ObjectMapper()
    def jsonPayloadService = new JsonPayloadService(objectMapper)


    def "Should throw RuntimeException if json is invalid when converting to payload"() {
        given: "JSON Nodes"
        def invalidJsonString = "{\"invalidField\": \"value\"}"

        when:
        jsonPayloadService.jsonToPayload([objectMapper.readTree(invalidJsonString)])

        then:
        def exception = thrown(RuntimeException)
        exception.getCause().getClass() == InvalidProtocolBufferException.class
    }

    def "Should throw RuntimeException if payload is invalid when converting to json"() {
        given: "mocked objectMapper"
        def objectMapperMock = Mock(ObjectMapper)
        def jsonPayloadService = new JsonPayloadService(objectMapperMock)

        and: "object mapper throws exception"
        objectMapperMock.readTree(_ as String) >> { throw new InvalidProtocolBufferException("test-exception") }

        and: "payload"
        def payload = Payload.newBuilder().build()

        when: "Converting payload to json"
        jsonPayloadService.payloadToJson([payload])

        then:
        def exception = thrown(RuntimeException)

        and:
        exception.getCause().getClass() == InvalidProtocolBufferException.class
        exception.getCause().getMessage() == "test-exception"
    }
}
