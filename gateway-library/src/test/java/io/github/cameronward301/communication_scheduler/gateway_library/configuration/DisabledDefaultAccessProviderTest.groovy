package io.github.cameronward301.communication_scheduler.gateway_library.configuration

import io.github.cameronward301.communication_scheduler.gateway_library.history.DefaultCommunicationHistoryAccessProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification


@ContextConfiguration(classes = [SharedGatewayConfiguration, DefaultCommunicationHistoryAccessProvider])
@TestPropertySource(properties = ["io.github.cameronward301.communication-scheduler.gateway-library.default-communication-history-access-provider.enabled=false"] )
class DisabledDefaultAccessProviderTest extends Specification {

    @Autowired(required = false)
    private DefaultCommunicationHistoryAccessProvider accessProvider

    def "Context loads"() {
        expect:
        accessProvider == null
    }
}
