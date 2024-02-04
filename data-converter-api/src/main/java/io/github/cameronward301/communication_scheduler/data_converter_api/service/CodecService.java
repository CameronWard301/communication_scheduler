package io.github.cameronward301.communication_scheduler.data_converter_api.service;

import com.google.protobuf.ByteString;
import io.github.cameronward301.communication_scheduler.data_converter_api.model.CodecDTO;
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.codec.CryptographyCodec;
import io.temporal.api.common.v1.Payload;
import io.temporal.common.converter.EncodingKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CodecService {

    public final ByteString METADATA_JSON =
            ByteString.copyFrom("json/plain", StandardCharsets.UTF_8);
    private final CryptographyCodec cryptographyCodec;
    private final JsonPayloadService jsonPayloadService;

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
        } catch (IllegalStateException e) {
            String message = "{\"Message\":\"Could not decrypt, Do the password and salt for the worker and data converter match? This message may have previously been encrypted with a different password and salt than previously used. " + e.getMessage() + "\"}";

            return new CodecDTO(jsonPayloadService.payloadToJson(List.of(Payload.newBuilder()
                    .putMetadata(EncodingKeys.METADATA_ENCODING_KEY, METADATA_JSON)
                    .setData(ByteString.copyFromUtf8(message)).build())));
        } catch (Exception e) {
            String message = "{\"Message\":\"Could not decrypt, unknown error: " + e.getMessage() + "\"}";

            return new CodecDTO(jsonPayloadService.payloadToJson(List.of(Payload.newBuilder()
                    .putMetadata(EncodingKeys.METADATA_ENCODING_KEY, METADATA_JSON)
                    .setData(ByteString.copyFromUtf8(message)).build())));
        }

    }

}
