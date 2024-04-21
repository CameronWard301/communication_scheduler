package io.github.cameronward301.communication_scheduler.preferences_api.properties;

import lombok.Data;
import lombok.Generated;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "cluster")
@Generated
public class ClusterPreferences {
    private String namespace;
}
