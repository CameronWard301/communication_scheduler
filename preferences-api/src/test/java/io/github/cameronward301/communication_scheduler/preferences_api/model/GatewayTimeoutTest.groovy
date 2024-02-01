package io.github.cameronward301.communication_scheduler.preferences_api.model

import spock.lang.Specification

class GatewayTimeoutTest extends Specification {

    def "No args constructor should create gatewayTimeout instance, required for PUT GatewayTimeout"(){
        when:
        def gatewayTimeout = new GatewayTimeout()

        then:
        gatewayTimeout != null
    }
}
