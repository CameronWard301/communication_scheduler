package io.github.cameronward301.communication_scheduler.workflows.communication_workflow.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * This class is used to configure AWS clients e.g. DynamoDb
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "aws-properties")
public class AwsProperties {
    //The AWS DynamoDb Table Name
    private String table_name;

    //The AWS DynamoDb Table Key Name (id)
    private String key_name;
}
