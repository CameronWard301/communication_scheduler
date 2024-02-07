package io.github.cameronward301.communication_scheduler.data_converter_api.controller


import com.fasterxml.jackson.databind.ObjectMapper
import com.google.protobuf.ByteString
import com.google.protobuf.util.JsonFormat
import io.github.cameronward301.communication_scheduler.data_converter_api.model.CodecDTO
import io.github.cameronward301.communication_scheduler.data_converter_api.service.CodecService
import io.temporal.api.common.v1.Payload
import org.springframework.http.HttpStatus
import spock.lang.Specification

class ConverterControllerTest extends Specification {
    def codecService = Mock(CodecService)
    def converterController = new ConverterController(codecService)
    def objectMapper = new ObjectMapper()

    def "Should decode codec DTO with status 200"() {
        given: "Payload consists of data"
        def payload = Payload.newBuilder().setData(ByteString.copyFrom("test-data".getBytes())).build()
        def codecDto = new CodecDTO(List.of(objectMapper.readTree(JsonFormat.printer().print(payload))))

        and:
        codecService.encrypt(codecDto) >> codecDto

        when: "encode is called"
        def response = converterController.encode(codecDto)

        then:
        response.getStatusCode() == HttpStatus.OK
        response.getBody() == codecDto
    }

    def "Should encode codec DTO with status 200"() {
        given: "Payload consists of data"
        def payload = Payload.newBuilder().setData(ByteString.copyFrom("test-data".getBytes())).build()
        def codecDto = new CodecDTO(List.of(objectMapper.readTree(JsonFormat.printer().print(payload))))

        and:
        codecService.decrypt(codecDto) >> codecDto

        when: "decode is called"
        def response = converterController.decode(codecDto)

        then:
        response.getStatusCode() == HttpStatus.OK
        response.getBody() == codecDto
    }
}
