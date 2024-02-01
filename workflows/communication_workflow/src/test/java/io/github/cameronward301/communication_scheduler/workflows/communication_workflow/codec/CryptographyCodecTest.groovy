package io.github.cameronward301.communication_scheduler.workflows.communication_workflow.codec

import com.google.protobuf.ByteString
import io.temporal.api.common.v1.Payload
import io.temporal.common.converter.EncodingKeys
import spock.lang.Specification

import static java.nio.charset.StandardCharsets.UTF_8

class CryptographyCodecTest extends Specification {
    private final ByteString METADATA_ENCODING =
            ByteString.copyFrom("binary/encrypted", UTF_8)


    private final ByteString METADATA_ENCRYPTION_CIPHER =
            ByteString.copyFrom("AES/GCM/NoPadding", UTF_8)

    private CryptographyCodec codec

    def setup() {
        def config = new EncryptorConfig()
        codec = new CryptographyCodec(config.bytesEncryptor("1234", "1234"))
    }


    def "Should encrypt payload list"() {
        given:
        def data = "test-data"
        def payload = Payload.newBuilder()
                .setData(ByteString.copyFrom(data.getBytes()))
                .build()

        when:
        def encryptedPayloads = codec.encode(List.of(payload))

        then:
        encryptedPayloads.size() == 1
        encryptedPayloads.get(0).getMetadataOrDefault("encoding", null).toStringUtf8() == "binary/encrypted"
        encryptedPayloads.get(0).getMetadataOrDefault("encryption-cipher", null).toStringUtf8() == "AES/GCM/NoPadding"
        !encryptedPayloads.get(0).getData().toStringUtf8().contains("test-data")
    }

    def "Should encrypt and decrypt payload list"() {
        given:
        def data = "test-data"
        def payload = Payload.newBuilder()
                .setData(ByteString.copyFrom(data.getBytes()))
                .build()

        def encryptedPayloads = codec.encode(List.of(payload))
        when:
        def decryptedPayloads = codec.decode(encryptedPayloads)

        then:
        decryptedPayloads.size() == 1
        decryptedPayloads.get(0).getMetadataOrDefault("encoding", null).toStringUtf8() == "json/plain"
        decryptedPayloads.get(0).getMetadataOrDefault("encryption-cipher", null) == null
        decryptedPayloads.get(0).getData().toStringUtf8().contains("test-data")
    }

    def "Should return same payload if not encrypted"() {
        given:
        def data = "test-data"
        def payload = Payload.newBuilder()
                .setData(ByteString.copyFrom(data.getBytes()))
                .build()

        when:
        def decryptedPayloads = codec.decode(List.of(payload))

        then:
        decryptedPayloads.size() == 1
        decryptedPayloads.get(0) == payload
    }

    def "Should throw exception if data is null when encrypting payload"() {
        given:
        def payload = GroovyMock(Payload)

        and:
        payload.getData() >> { throw new Exception("Test exception") }

        when:
        codec.encode(List.of(payload))

        then:
        thrown(Throwable)
    }

    def "Should throw exception if data is null when decrypting payload"() {
        given:
        String METADATA_ENCRYPTION_CIPHER_KEY = "encryption-cipher"
        def payload = Payload.newBuilder()
                .putMetadata(EncodingKeys.METADATA_ENCODING_KEY, METADATA_ENCODING)
                .putMetadata(METADATA_ENCRYPTION_CIPHER_KEY, METADATA_ENCRYPTION_CIPHER)
                .build()

        and:
        payload.getMetadataOrDefault(_ as String, null) >> { throw new Exception("Test exception") }

        when:
        codec.decode(List.of(payload))

        then:
        thrown(Throwable)


    }

}
