package io.github.cameronward301.communication_scheduler.mock_gateway.controller

import io.github.cameronward301.communication_scheduler.gateway_library.history.CommunicationHistoryAccessProvider
import io.github.cameronward301.communication_scheduler.gateway_library.model.GatewayRequest
import io.github.cameronward301.communication_scheduler.gateway_library.model.GatewayResponse
import io.github.cameronward301.communication_scheduler.gateway_library.properties.GatewayProperties
import io.github.cameronward301.communication_scheduler.gateway_library.service.CommunicationGatewayService
import io.github.cameronward301.communication_scheduler.mock_gateway.service.MockDeliveryService
import io.github.cameronward301.communication_scheduler.mock_gateway.service.MockUserContentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Specification

class MockGatewayControllerTest extends Specification {

    def communicationGatewayService = Mock(CommunicationGatewayService)
    def communicationHistoryAccessProvider = Mock(CommunicationHistoryAccessProvider)
    def mockUserContentService = Mock(MockUserContentService)
    def mockDeliveryService = Mock(MockDeliveryService)

    private MockGatewayController mockGatewayController

    def setup() {
        mockGatewayController = new MockGatewayController(communicationGatewayService, communicationHistoryAccessProvider, mockUserContentService, mockDeliveryService)
    }

    def "should return 200 when controller is called"() {
        given:
        GatewayRequest gatewayRequest = new GatewayRequest("test", "test-run")

        and: "service returns response entity"
        communicationGatewayService.sendCommunication(_ as GatewayProperties) >> new ResponseEntity<GatewayResponse>(GatewayResponse.builder().messageHash("test-hash").userId("test").build(), HttpStatus.OK)

        when:
        def response = mockGatewayController.processGatewayRequest(gatewayRequest)

        then:
        response.getStatusCode() == HttpStatus.OK
        response.getBody().userId == "test"
        response.getBody().messageHash == "test-hash"
    }
}
