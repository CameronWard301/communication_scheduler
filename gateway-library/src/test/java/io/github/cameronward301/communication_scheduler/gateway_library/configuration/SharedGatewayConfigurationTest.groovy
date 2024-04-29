package io.github.cameronward301.communication_scheduler.gateway_library.configuration

import io.github.cameronward301.communication_scheduler.gateway_library.properties.DefaultCommunicationHistoryAccessProviderProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import spock.lang.Specification

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

        @Bean
        SecurityConfigurationProperties sharedConfigurationProperties() {
            SecurityConfigurationProperties.Cors cors = new SecurityConfigurationProperties.Cors(true)
            SecurityConfigurationProperties.Csrf csrf = new SecurityConfigurationProperties.Csrf(false)
            SecurityConfigurationProperties sharedConfigurationProperties = new SecurityConfigurationProperties()
            sharedConfigurationProperties.setCors(cors)
            sharedConfigurationProperties.setCsrf(csrf)
            return sharedConfigurationProperties
        }
    }
}
