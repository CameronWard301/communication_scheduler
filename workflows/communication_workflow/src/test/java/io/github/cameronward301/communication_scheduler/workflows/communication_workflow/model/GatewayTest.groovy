package io.github.cameronward301.communication_scheduler.workflows.communication_workflow.model

import spock.lang.Specification

class GatewayTest extends Specification {

    def "Should create gateway without exception"() {
        given:
        String id = "test-id"
        String endpointUrl = "test-endpoint-url"

        when:
        Gateway gateway = new Gateway(id, endpointUrl)

        then:
        notThrown(Exception)
        gateway.getId() == id
        gateway.getEndpointUrl() == endpointUrl
    }
}
