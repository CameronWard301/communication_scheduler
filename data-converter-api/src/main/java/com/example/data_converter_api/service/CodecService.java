package com.example.data_converter_api.service;

import com.example.data_converter_api.model.CodecDTO;
import com.google.protobuf.ByteString;
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.codec.CryptographyCodec;
import io.temporal.api.common.v1.Payload;
import io.temporal.common.converter.EncodingKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import javax.crypto.AEADBadTagException;
import javax.crypto.BadPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CodecService {

    private final CryptographyCodec cryptographyCodec;
    private final JsonPayloadService jsonPayloadService;
    public final ByteString METADATA_JSON =
            ByteString.copyFrom("json/plain", StandardCharsets.UTF_8);

    public CodecDTO encrypt(CodecDTO codec) {
        return new CodecDTO(jsonPayloadService.payloadToJson(
                cryptographyCodec.encode(
                        jsonPayloadService.jsonToPayload(codec.getPayloads())
                )
        ));
    }

    public CodecDTO decrypt(CodecDTO codec) {
        try {
            return new CodecDTO(jsonPayloadService.payloadToJson(
                    cryptographyCodec.decode(
                            jsonPayloadService.jsonToPayload(codec.getPayloads())
                    )
            ));
        } catch (Exception e) {
            String message = getString(e);

            return new CodecDTO(jsonPayloadService.payloadToJson(List.of(Payload.newBuilder()
                    .putMetadata(EncodingKeys.METADATA_ENCODING_KEY, METADATA_JSON)
                    .setData(ByteString.copyFromUtf8(message)).build())));
        }

    }

    private static String getString(Exception e) {
        String message;
        if (Objects.equals(e.getMessage(), "Tag mismatch") || e.getCause().getClass() == IllegalStateException.class) {
            message = "{\"Message\":\"Could not decrypt, Do the password and salt for the worker and data converter match? This message may have previously been encrypted with a different password and salt than previously used. " + e.getMessage() + "\"}";
        } else {
            message = "{\"Message\":\"Could not decrypt, unknown error: " + e.getMessage() + "\"}";
        }
        return message;
    }

}
