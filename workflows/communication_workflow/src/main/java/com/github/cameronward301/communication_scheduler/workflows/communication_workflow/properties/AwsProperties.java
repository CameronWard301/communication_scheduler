package com.github.cameronward301.communication_scheduler.workflows.communication_workflow.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "aws-properties")
public class AwsProperties {
    private String table_name;
    private String key_name;
}
