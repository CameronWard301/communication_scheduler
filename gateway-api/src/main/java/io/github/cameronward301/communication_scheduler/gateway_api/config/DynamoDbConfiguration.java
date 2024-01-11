package io.github.cameronward301.communication_scheduler.gateway_api.config;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for DynamoDB
 */
@Configuration
public class DynamoDbConfiguration {

    /**
     * Creates a DynamoDBMapper bean with AWS credentials from the environment
     *
     * @return DynamoDBMapper bean
     */
    @Bean
    public DynamoDBMapper dynamoDBMapper() {
        return new DynamoDBMapper(AmazonDynamoDBClientBuilder
                .standard()
                .withRegion("eu-west-1")
                .build());
    }
}
