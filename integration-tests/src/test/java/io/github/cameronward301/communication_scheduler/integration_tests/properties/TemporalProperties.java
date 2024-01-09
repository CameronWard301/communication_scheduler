package io.github.cameronward301.communication_scheduler.integration_tests.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "temporal-connection")
@Data
public class TemporalProperties {
    private String namespace;
    private String host;
    private String taskQueue;
}
