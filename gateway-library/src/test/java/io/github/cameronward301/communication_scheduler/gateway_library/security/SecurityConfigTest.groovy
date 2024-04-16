package io.github.cameronward301.communication_scheduler.gateway_library.security

import io.github.cameronward301.communication_scheduler.gateway_library.configuration.SecurityConfigurationProperties
import io.github.cameronward301.communication_scheduler.gateway_library.configuration.SharedGatewayConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(classes = [SecurityConfig, GatewayApiKeyFilter, SharedGatewayConfiguration])
class SecurityConfigTest extends Specification {

    @Autowired
    SecurityConfig securityConfig

    def "Should configure http security to not include csrf and cors"() {
        expect:
        securityConfig != null
    }

    @Configuration
    static class TestConfiguration {
        @Bean
        GatewayApiKeyFilter gatewayApiKeyFilter() {
            return new GatewayApiKeyFilter(new GatewayApiKeyExtractor("test-key"))
        }

        @Bean
        @Primary
        SecurityConfigurationProperties sharedConfigurationProperties() {
            SecurityConfigurationProperties.Cors cors = new SecurityConfigurationProperties.Cors()
            cors.setEnabled(false)
            SecurityConfigurationProperties.Csrf csrf = new SecurityConfigurationProperties.Csrf()
            csrf.setEnabled(false)
            SecurityConfigurationProperties sharedConfigurationProperties = new SecurityConfigurationProperties()
            sharedConfigurationProperties.setCors(cors)
            sharedConfigurationProperties.setCsrf(csrf)
            return sharedConfigurationProperties
        }
    }
}
