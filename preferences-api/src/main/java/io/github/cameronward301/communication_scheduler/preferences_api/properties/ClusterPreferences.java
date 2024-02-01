package io.github.cameronward301.communication_scheduler.preferences_api.properties;

import lombok.Builder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "cluster")
public class ClusterPreferences {
    private String namespace;
}
