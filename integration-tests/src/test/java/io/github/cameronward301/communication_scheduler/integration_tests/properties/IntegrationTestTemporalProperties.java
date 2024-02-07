package io.github.cameronward301.communication_scheduler.integration_tests.properties;

import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.properties.TemporalProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@EqualsAndHashCode(callSuper = true)
@Configuration
@ConfigurationProperties(prefix = "temporal-connection")
@Data
public class IntegrationTestTemporalProperties extends TemporalProperties {
    private String host;
    private String taskQueue;
}
