package io.github.cameronward301.communication_scheduler.gateway_library.controller

import io.github.cameronward301.communication_scheduler.gateway_library.model.GatewayRequest
import io.github.cameronward301.communication_scheduler.gateway_library.model.GatewayResponse
import io.github.cameronward301.communication_scheduler.gateway_library.properties.GatewayProperties
import io.github.cameronward301.communication_scheduler.gateway_library.service.CommunicationGatewayService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Specification

class GatewayControllerTest extends Specification {
    def "should return 200 when GatewayService is called"() {
        given:
        GatewayProperties gatewayProperties = Mock(GatewayProperties)
        CommunicationGatewayService communicationGatewayService = Mock(CommunicationGatewayService)

        GatewayResponse gatewayResponse = GatewayResponse.builder().userId("123").messageHash("abc").build()

        communicationGatewayService.sendCommunication(gatewayProperties) >> new ResponseEntity<>(gatewayResponse, HttpStatus.OK)
        def gatewayController = new GatewayControllerImpl(gatewayProperties, communicationGatewayService)

        and:
        GatewayRequest gatewayRequest = new GatewayRequest("123", "213")

        when:
        def response = gatewayController.processGatewayRequest(gatewayRequest)

        then:
        response.getStatusCode() == HttpStatus.OK
    }


}
