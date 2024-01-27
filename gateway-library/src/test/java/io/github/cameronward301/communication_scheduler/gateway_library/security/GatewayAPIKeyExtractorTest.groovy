package io.github.cameronward301.communication_scheduler.gateway_library.security

import jakarta.servlet.http.HttpServletRequest
import spock.lang.Specification

class GatewayAPIKeyExtractorTest extends Specification {
    def KEY = "1234"
    private GatewayApiKeyExtractor apiKeyExtractor
    def request = Mock(HttpServletRequest)

    def setup() {
        apiKeyExtractor = new GatewayApiKeyExtractor(KEY)
    }

    def "Should extract key from header"(){
        given: "Request contains api key"
        request.getHeader("x-worker-api-key") >> KEY

        when: "Call get key method"
        def result = apiKeyExtractor.getKey(request)

        then: "Authentication is not empty"
        !result.isEmpty()
    }


    def "Should not authenticate if key is wrong"(){
        given: "Request contains api key"
        request.getHeader("x-worker-api-key") >> "5678"

        when: "Call get key method"
        def result = apiKeyExtractor.getKey(request)

        then: "Authentication is not empty"
        result.isEmpty()
    }


}
