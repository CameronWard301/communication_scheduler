package io.github.cameronward301.communication_scheduler.gateway_library.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import spock.lang.Specification

class GatewayApiKeyFilterTest extends Specification {

    def "Should set authentication if API key is valid"() {
        given: "Mocked dependencies"
        def keyExtractor = Mock(GatewayApiKeyExtractor)
        def keyFilter = new GatewayApiKeyFilter(keyExtractor)
        def request = Mock(HttpServletRequest)
        def response = new MockHttpServletResponse()

        and: "get key returns valid key"
        keyExtractor.getKey(_ as HttpServletRequest) >> Optional.of(new GatewayApiKey("valid-key", AuthorityUtils.NO_AUTHORITIES))

        when:
        keyFilter.doFilterInternal(request, response, Mock(FilterChain))

        then:
        notThrown(Exception)

    }
}
