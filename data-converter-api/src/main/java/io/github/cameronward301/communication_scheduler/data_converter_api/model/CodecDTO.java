package io.github.cameronward301.communication_scheduler.data_converter_api.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodecDTO {
    private List<JsonNode> payloads;
}
