package com.github.cameronward301.communication_scheduler.workflows.communication_workflow.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "temporal-properties")
@Data
public class TemporalProperties {
    private String task_queue;
    private String namespace;
}
