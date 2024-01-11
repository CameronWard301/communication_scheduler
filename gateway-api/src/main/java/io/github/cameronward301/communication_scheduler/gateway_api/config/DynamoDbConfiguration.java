package io.github.cameronward301.communication_scheduler.gateway_api.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for DynamoDB
 */
@Configuration
public class DynamoDbConfiguration {
    @Value("${gateway.dynamodb.table.name}")
    String tableName;

    @Value("${aws.region}")
    String region;

    /**
     * Creates a DynamoDBMapper bean with AWS credentials from the environment
     *
     * @return DynamoDBMapper bean
     */
    @Bean
    public DynamoDBMapper dynamoDBMapper() {
        DynamoDBMapperConfig dynamoDBMapperConfig = DynamoDBMapperConfig.builder()
                .withTableNameOverride(tableNameOverrider())
                .build();

        return new DynamoDBMapper(AmazonDynamoDBClientBuilder
                .standard()
                .withRegion(region)
                .build(), dynamoDBMapperConfig);
    }

    @Bean
    public DynamoDBMapperConfig.TableNameOverride tableNameOverrider() {
        return DynamoDBMapperConfig.TableNameOverride.withTableNameReplacement(tableName);
    }
}
