package io.github.cameronward301.communication_scheduler.gateway_api.model

import spock.lang.Specification

class GatewayTest extends Specification {
    def "No args constructor should set description"() {
        given:
        def gateway = new Gateway()

        expect:
        gateway.getDescription() == ""
    }
}
