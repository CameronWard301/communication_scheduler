package io.github.cameronward301.communication_scheduler.data_converter_api.model

import com.google.protobuf.util.JsonFormat
import spock.lang.Specification

class CodecDTOTest extends Specification {

    def "Should create CodecDTO"() {
        when:
        def dto = new CodecDTO()

        then:
        dto != null
    }

    def "Should get and set DTO field"() {
        given:
        def dto = new CodecDTO()

        when:
        dto.setPayloads([JsonFormat.parser().print("hello")])

        then:
        dto.getPayloads().size() == 1
    }
}
