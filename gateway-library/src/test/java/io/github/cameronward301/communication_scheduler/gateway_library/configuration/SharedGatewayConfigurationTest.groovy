package io.github.cameronward301.communication_scheduler.gateway_library.configuration

import io.github.cameronward301.communication_scheduler.gateway_library.properties.DefaultCommunicationHistoryAccessProviderProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import spock.lang.Specification

@TestPropertySource(properties = [
        "security.cors.enabled=true",
        "security.csrf.enabled=true"
])
@ContextConfiguration(classes = [SharedGatewayConfiguration])
class SharedGatewayConfigurationTest extends Specification {

    @Autowired
    DynamoDbAsyncClient dynamoDbAsyncClient

    def "dynamoDbClient Should Be Created"() {
        expect:
        dynamoDbAsyncClient != null
    }


    @Configuration
    static class TestConfiguration {
        @Bean
        DefaultCommunicationHistoryAccessProviderProperties defaultCommunicationHistoryAccessProviderProperties() {
            DefaultCommunicationHistoryAccessProviderProperties defaultCommunicationHistoryAccessProviderProperties = new DefaultCommunicationHistoryAccessProviderProperties()
            defaultCommunicationHistoryAccessProviderProperties.setRegion("eu-west-1")
            return defaultCommunicationHistoryAccessProviderProperties
        }
    }
}
