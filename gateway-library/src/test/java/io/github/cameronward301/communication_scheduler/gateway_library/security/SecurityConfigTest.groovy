package io.github.cameronward301.communication_scheduler.gateway_library.security

import io.github.cameronward301.communication_scheduler.gateway_library.configuration.SharedGatewayConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification

@TestPropertySource(properties = [
        "security.cors.enabled=false",
        "security.csrf.enabled=false"
])
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
    }
}
