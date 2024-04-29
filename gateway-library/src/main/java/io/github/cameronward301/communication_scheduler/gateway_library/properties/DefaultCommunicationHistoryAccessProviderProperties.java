package io.github.cameronward301.communication_scheduler.gateway_library.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Properties for the default communication history access provider
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "io.github.cameronward301.communication-scheduler.gateway-library.default-communication-history-access-provider")
public class DefaultCommunicationHistoryAccessProviderProperties {

    /**
     * The name of the database table to use for the communication history
     */
    private String table_name;
    private String region;
}
