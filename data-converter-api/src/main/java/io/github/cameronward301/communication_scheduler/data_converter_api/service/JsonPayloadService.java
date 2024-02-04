package io.github.cameronward301.communication_scheduler.data_converter_api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.temporal.api.common.v1.Payload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JsonPayloadService {

    private final ObjectMapper objectMapper;

    public List<Payload> jsonToPayload(List<JsonNode> jsonNodes) {

        return jsonNodes.stream().map(jsonNode -> {
            Payload.Builder payloadBuilder = Payload.newBuilder();
            try {
                JsonFormat.parser().merge(jsonNode.toString(), payloadBuilder);
            } catch (InvalidProtocolBufferException e) {
                throw new RuntimeException(e);
            }
            return payloadBuilder.build();
        }).collect(Collectors.toList());
    }

    public List<JsonNode> payloadToJson(List<Payload> payloads) {
        return payloads.stream().map(payload -> {
            try {
                return objectMapper.readTree(JsonFormat.printer().print(payload));
            } catch (JsonProcessingException | InvalidProtocolBufferException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }
}
