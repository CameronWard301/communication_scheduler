package io.github.cameronward301.communication_scheduler.gateway_library.model

import spock.lang.Specification

class GatewayRequestTest extends Specification {
    def "test GatewayRequest properties"() {
        given:
        final String userId = "test-uer"
        final String workflowRunId = "test-run-id"

        when:
        def gatewayRequest = new GatewayRequest(userId, workflowRunId)

        then:
        gatewayRequest.userId() == userId
        gatewayRequest.workflowRunId() == workflowRunId
    }
}
