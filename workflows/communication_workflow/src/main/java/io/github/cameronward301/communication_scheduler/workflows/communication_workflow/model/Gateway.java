package io.github.cameronward301.communication_scheduler.workflows.communication_workflow.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@AllArgsConstructor
@Builder
public class Gateway {
    @Id
    private String id;

    private String endpointUrl;
}
