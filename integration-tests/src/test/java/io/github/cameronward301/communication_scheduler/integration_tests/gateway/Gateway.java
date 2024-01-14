package io.github.cameronward301.communication_scheduler.integration_tests.gateway;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document()
public class Gateway {
    @Id
    private String id;
    private String endpointUrl;
    private String friendlyName;
    private String description = "";
    private String dateCreated;
}
