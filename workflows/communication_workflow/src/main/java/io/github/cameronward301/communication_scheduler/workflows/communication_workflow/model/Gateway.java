package io.github.cameronward301.communication_scheduler.workflows.communication_workflow.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Holds the gateway information for the worker from mongoDB
 */
@Document
@Getter
@AllArgsConstructor
@Builder
public class Gateway {
    @Id
    private String id;

    private String endpointUrl;
}
