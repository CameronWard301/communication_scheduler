package com.github.cameronward301.communication_scheduler.workflows.communication_workflow.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Stores the properties for Temporal workflows and workers
 */
@Configuration
@ConfigurationProperties(prefix = "temporal-properties")
@Data
public class TemporalProperties {
    private String namespace;
}
