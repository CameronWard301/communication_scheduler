package io.github.cameronward301.communication_scheduler.data_converter_api.service


import com.fasterxml.jackson.databind.ObjectMapper
import com.google.protobuf.ByteString
import com.google.protobuf.util.JsonFormat
import io.github.cameronward301.communication_scheduler.data_converter_api.model.CodecDTO
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.codec.CryptographyCodec
import io.temporal.api.common.v1.Payload
import io.temporal.common.converter.EncodingKeys
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import static java.nio.charset.StandardCharsets.UTF_8

@SpringBootTest
class CodecServiceTest extends Specification {
    private CodecService codecService
    def objectMapper = new ObjectMapper()

    private final ByteString METADATA_JSON =
            ByteString.copyFrom("json/plain", UTF_8)

    private final ByteString METADATA_ENCRYPTION_CIPHER =
            ByteString.copyFrom("AES/GCM/NoPadding", UTF_8)

    private final String METADATA_ENCRYPTION_CIPHER_KEY = "encryption-cipher"

    @Autowired
    private CryptographyCodec cryptographyCodec

    @Autowired
    private JsonPayloadService jsonPayloadService

    def setup() {
        codecService = new CodecService(cryptographyCodec, jsonPayloadService)
    }

    def "Should encrypt codecDTO"() {
        given: "CodecDTO"
        def payload = Payload.newBuilder().setData(ByteString.copyFrom("test-data".getBytes())).build()
        def codecDto = new CodecDTO(List.of(objectMapper.readTree(JsonFormat.printer().print(payload))))

        when: "encrypting the codecDTO"
        def encryptedDto = codecService.encrypt(codecDto)

        then: "result has one payload"
        encryptedDto.getPayloads().size() == 1

        and: "metadata is set"
        jsonPayloadService.jsonToPayload(encryptedDto.getPayloads()).get(0).getMetadataOrDefault(EncodingKeys.METADATA_ENCODING_KEY, null).toStringUtf8() == "binary/encrypted"
        jsonPayloadService.jsonToPayload(encryptedDto.getPayloads()).get(0).getMetadataOrDefault(METADATA_ENCRYPTION_CIPHER_KEY, null) == METADATA_ENCRYPTION_CIPHER

        and: "result is encrypted"
        jsonPayloadService.jsonToPayload(encryptedDto.getPayloads()).get(0).getData() != ByteString.copyFrom("test-data".getBytes())
    }

    def "Should decrypt codecDTO"() {
        given: "CodecDTO"
        def payload = Payload.newBuilder().setData(ByteString.copyFrom("test-data".getBytes())).build()
        def codecDto = new CodecDTO(List.of(objectMapper.readTree(JsonFormat.printer().print(payload))))

        and: "encrypting the codecDTO"
        def encryptedDto = codecService.encrypt(codecDto)

        when: "decrypting the codecDTO"
        def decryptedDTO = codecService.decrypt(encryptedDto)

        then: "result has one payload"
        decryptedDTO.getPayloads().size() == 1

        and: "decrypted payload has test data"
        jsonPayloadService.jsonToPayload(decryptedDTO.getPayloads()).get(0).getData() == ByteString.copyFrom("test-data".getBytes())

        and: "headers are set"
        jsonPayloadService.jsonToPayload(decryptedDTO.getPayloads()).get(0).getMetadataOrDefault(EncodingKeys.METADATA_ENCODING_KEY, null) == METADATA_JSON
    }

    def "Should return error codec if cryptography codec throws IllegalStateException exception"() {
        given: "Mocked jsonPayloadService"
        def cryptographyMock = Mock(CryptographyCodec)
        def codecService = new CodecService(cryptographyMock, jsonPayloadService)

        and: "CodecDTO"
        def payload = Payload.newBuilder().setData(ByteString.copyFrom("test-data".getBytes())).build()
        def codecDto = new CodecDTO(List.of(objectMapper.readTree(JsonFormat.printer().print(payload))))

        and: "json service throws exception"
        cryptographyMock.decode(_ as List<Payload>) >> { throw new IllegalStateException("Tag mismatch") }

        and: "error message should be"
        def message = "{\"Message\":\"Could not decrypt, Do the password and salt for the worker and data converter match? This message may have previously been encrypted with a different password and salt than previously used. Tag mismatch\"}"

        when: "Decrypting codec"
        def response = codecService.decrypt(codecDto)

        then: "Response contains correct error message"
        jsonPayloadService.jsonToPayload(response.getPayloads()).get(0).getData().toStringUtf8() == message

    }

    def "Should return generic error codec if cryptography codec throws other exception"() {
        given: "Mocked jsonPayloadService"
        def cryptographyMock = Mock(CryptographyCodec)
        def codecService = new CodecService(cryptographyMock, jsonPayloadService)

        and: "CodecDTO"
        def payload = Payload.newBuilder().setData(ByteString.copyFrom("test-data".getBytes())).build()
        def codecDto = new CodecDTO(List.of(objectMapper.readTree(JsonFormat.printer().print(payload))))

        and: "json service throws exception"
        cryptographyMock.decode(_ as List<Payload>) >> { throw new Exception("other exception") }

        and: "error message should be"
        def message = "{\"Message\":\"Could not decrypt, unknown error: other exception\"}"

        when: "Decrypting codec"
        def response = codecService.decrypt(codecDto)

        then: "Response contains correct error message"
        jsonPayloadService.jsonToPayload(response.getPayloads()).get(0).getData().toStringUtf8() == message

    }
}
