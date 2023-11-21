package com.github.cameronward301.communication_scheduler.workflows.communication_workflow.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 * This class is used to store the properties for Temporal activities
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "activity-properties")
public class ActivityProperties {
    // The number of seconds to wait for a response from the gateway before timing out
    private int gateway_timeout_seconds;
}
