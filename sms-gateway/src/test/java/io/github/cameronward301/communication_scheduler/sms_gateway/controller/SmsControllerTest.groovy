package io.github.cameronward301.communication_scheduler.sms_gateway.controller

import io.github.cameronward301.communication_scheduler.gateway_library.history.DefaultCommunicationHistoryAccessProvider
import io.github.cameronward301.communication_scheduler.gateway_library.model.GatewayRequest
import io.github.cameronward301.communication_scheduler.gateway_library.model.GatewayResponse
import io.github.cameronward301.communication_scheduler.gateway_library.properties.GatewayProperties
import io.github.cameronward301.communication_scheduler.gateway_library.service.CommunicationGatewayService
import io.github.cameronward301.communication_scheduler.sms_gateway.service.SmsUserContentService
import io.github.cameronward301.communication_scheduler.sms_gateway.service.SmsWeeklyReportDeliveryService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Specification

class SmsControllerTest extends Specification {
    private SmsController smsController
    private SmsUserContentService userContentService = Mock(SmsUserContentService)
    private SmsWeeklyReportDeliveryService contentDeliveryService = Mock(SmsWeeklyReportDeliveryService)
    private DefaultCommunicationHistoryAccessProvider communicationHistoryAccessProvider = Mock(DefaultCommunicationHistoryAccessProvider)
    private CommunicationGatewayService gatewayService = Mock(CommunicationGatewayService)

    def setup() {
        smsController = new SmsController(gatewayService, communicationHistoryAccessProvider, contentDeliveryService, userContentService)
    }

    def "Should return 200 when SmsUserContentService is called"() {
        given:
        GatewayRequest smsRequest = new GatewayRequest("test-user-id", "test-run-id")
        gatewayService.sendCommunication(_ as GatewayProperties) >> new ResponseEntity<>(GatewayResponse.builder().messageHash("test-hash").userId("test-user-id").build(), HttpStatus.OK)
        when:
        def response = smsController.processGatewayRequest(smsRequest)

        then:
        response.getStatusCode() == HttpStatus.OK
        response.getBody().userId == "test-user-id"
        response.getBody().messageHash == "test-hash"
    }

}
