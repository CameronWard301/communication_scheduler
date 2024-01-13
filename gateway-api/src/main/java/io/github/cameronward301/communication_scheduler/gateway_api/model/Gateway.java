package io.github.cameronward301.communication_scheduler.gateway_api.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Gateway {
    @Id
    private String id;
    @NotBlank(message = "'endpointUrl' cannot be empty")
    private String endpointUrl;
    @NotBlank(message = "'friendlyName' cannot be empty")
    private String friendlyName;
    private String description = "";
    private String dateCreated;
}
