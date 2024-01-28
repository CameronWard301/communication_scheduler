package io.github.cameronward301.communication_scheduler.gateway_library.security

import org.springframework.security.core.authority.AuthorityUtils
import spock.lang.Specification

class GatewayApiKeyTest extends Specification{

    private GatewayApiKey gatewayApiKey;
    private final String KEY = "1234"
    def setup() {
        gatewayApiKey = new GatewayApiKey(KEY, AuthorityUtils.NO_AUTHORITIES)
    }

    def "Should return api key"(){
        expect:
        gatewayApiKey.getPrincipal() == KEY

        and:
        gatewayApiKey.getCredentials() == null
    }
}
