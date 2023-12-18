package io.github.cameronward301.communication_scheduler.gateway_library.configuration;

import io.github.cameronward301.communication_scheduler.gateway_library.properties.DefaultCommunicationHistoryAccessProviderProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

@AutoConfiguration
@ComponentScan(basePackages = {"io.github.cameronward301.communication_scheduler.gateway_library"})
public class SharedGatewayConfiguration {
    @Bean
    @ConditionalOnProperty(prefix = "default-communication-history-access-provider", name = "enabled", havingValue = "true", matchIfMissing = true)
    public DynamoDbAsyncClient dynamoDbAsyncClient(DefaultCommunicationHistoryAccessProviderProperties properties) {
        return DynamoDbAsyncClient.builder()
                .region(Region.of(properties.getRegion()))
                .build();
    }
}
