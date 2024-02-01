package io.github.cameronward301.communication_scheduler.workflows.communication_workflow.codec;
// Adapted from: https://github.com/temporalio/samples-java/blob/main/core/src/main/java/io/temporal/samples/encryptedpayloads/CryptCodec.java

import com.google.protobuf.ByteString;
import io.temporal.api.common.v1.Payload;
import io.temporal.common.converter.EncodingKeys;
import io.temporal.payload.codec.PayloadCodec;
import io.temporal.payload.codec.PayloadCodecException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Creates a cryptography codec for the worker to use and the data converter API for the temporal UI
 */
@Component
@RequiredArgsConstructor
public class CryptographyCodec implements PayloadCodec {

    public final ByteString METADATA_JSON =
            ByteString.copyFrom("json/plain", StandardCharsets.UTF_8);
    private final ByteString METADATA_ENCODING =
            ByteString.copyFrom("binary/encrypted", StandardCharsets.UTF_8);
    private final ByteString METADATA_ENCRYPTION_CIPHER =
            ByteString.copyFrom("AES/GCM/NoPadding", UTF_8);

    private final BytesEncryptor bytesEncryptor;

    /**
     * For each list of payloads, encrypt them
     *
     * @param payloads the list of plain text payloads
     * @return a list of encrypted payloads
     */
    @Nonnull
    @Override
    public List<Payload> encode(@Nonnull List<Payload> payloads) {
        return payloads.stream().map(this::encryptPayload).collect(Collectors.toList());
    }

    /**
     * For each payload in the list, decrypt them
     *
     * @param payloads the list of encrypted payloads
     * @return the list of decrypted payloads
     */
    @Nonnull
    @Override
    public List<Payload> decode(@Nonnull List<Payload> payloads) {
        return payloads.stream().map(this::decryptPayload).collect(Collectors.toList());
    }

    /**
     * Encrypts the payload with a password and salt
     *
     * @param payload to encrypt
     * @return an encrypted payload
     */
    private Payload encryptPayload(Payload payload) {
        try {
            String METADATA_ENCRYPTION_CIPHER_KEY = "encryption-cipher";
            return Payload.newBuilder()
                    .putMetadata(EncodingKeys.METADATA_ENCODING_KEY, METADATA_ENCODING)
                    .putMetadata(METADATA_ENCRYPTION_CIPHER_KEY, METADATA_ENCRYPTION_CIPHER)
                    .setData(ByteString.copyFrom(bytesEncryptor.encrypt(payload.getData().toByteArray())))
                    .build();
        } catch (Throwable e) {
            throw new PayloadCodecException(e);
        }
    }

    /**
     * If the contents is not encrypted or with a different key, return the payload without attempting to decrypt
     *
     * @param payload the payload to possibly decrypt
     * @return a decrypted payload in plain text
     */
    private Payload decryptPayload(Payload payload) {
        try {
            if (METADATA_ENCODING.equals(payload.getMetadataOrDefault(EncodingKeys.METADATA_ENCODING_KEY, null))) {
                return Payload.newBuilder()
                        .putMetadata(EncodingKeys.METADATA_ENCODING_KEY, METADATA_JSON)
                        .setData(ByteString.copyFrom(bytesEncryptor.decrypt(payload.getData().toByteArray())))
                        .build();
            } else {
                return payload;
            }
        } catch (Throwable e) {
            throw new PayloadCodecException(e);
        }
    }
}
