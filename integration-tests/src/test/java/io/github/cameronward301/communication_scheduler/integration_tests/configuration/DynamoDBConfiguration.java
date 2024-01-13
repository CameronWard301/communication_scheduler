package io.github.cameronward301.communication_scheduler.integration_tests.configuration;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import io.github.cameronward301.communication_scheduler.integration_tests.gateway.GatewayDbModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration class for DynamoDB
 */
@Configuration
public class DynamoDBConfiguration {
    @Value("${gateway-api.dynamodb.table.name}")
    String tableName;

    @Value("${aws.region}")
    String region;

    @Value("${gateway-api.entity.id}")
    private String testGatewayId;

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

    /**
     * Creates a GatewayDbModel bean for use in tests
     * @return GatewayDbModel bean
     */
    @Bean
    public GatewayDbModel gatewayDbModel() {
        return GatewayDbModel.builder()
                .id(testGatewayId)
                .dateCreated("2021-01-01T00:00:00.000Z")
                .endpointUrl("https://test-gateway.com/monthly/newsletter")
                .description("test gateway description")
                .friendlyName("test gateway name")
                .build();
    }

    @Bean
    public List<GatewayDbModel> gatewayDbModels() {
        List<GatewayDbModel> gateways = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            gateways.add(GatewayDbModel.builder()
                    .id(testGatewayId + "-" + (i + 1))
                    .dateCreated("2021-01-01T00:00:00.000Z")
                    .endpointUrl("https://test-gateway-" + (i + 1) + ".com/monthly/newsletter")
                    .description("test gateway " + (i + 1) + " description")
                    .friendlyName("test gateway " + (i + 1) + " name")
                    .build());
        }
        return gateways;
    }
}
