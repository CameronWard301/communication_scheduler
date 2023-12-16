package io.github.cameronward301.communication_scheduler.email_gateway.controller

import io.github.cameronward301.communication_scheduler.email_gateway.service.EmailMonthlyReportContentDeliveryService
import io.github.cameronward301.communication_scheduler.email_gateway.service.EmailUserContentService
import io.github.cameronward301.communication_scheduler.gateway_library.history.DefaultCommunicationHistoryAccessProvider
import io.github.cameronward301.communication_scheduler.gateway_library.model.GatewayRequest
import io.github.cameronward301.communication_scheduler.gateway_library.model.GatewayResponse
import io.github.cameronward301.communication_scheduler.gateway_library.properties.GatewayProperties
import io.github.cameronward301.communication_scheduler.gateway_library.service.CommunicationGatewayService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Specification

class EmailControllerTest extends Specification {
    private EmailController emailController
    private EmailUserContentService userContentService = Mock(EmailUserContentService)
    private EmailMonthlyReportContentDeliveryService contentDeliveryService = Mock(EmailMonthlyReportContentDeliveryService)
    private DefaultCommunicationHistoryAccessProvider communicationHistoryAccessProvider = Mock(DefaultCommunicationHistoryAccessProvider)
    private CommunicationGatewayService gatewayService = Mock(CommunicationGatewayService)

    def setup(){
        emailController = new EmailController(contentDeliveryService, userContentService, communicationHistoryAccessProvider, gatewayService)
    }

    def "should return 200 when EmailUserContentService is called"() {
        given:
        GatewayRequest emailRequest = new GatewayRequest("test-user-id", "test-run-id")
        gatewayService.sendCommunication(_ as GatewayProperties) >> new ResponseEntity<>(GatewayResponse.builder().messageHash("test-hash").userId("test-user-id").build(), HttpStatus.OK)
        when:
        def response = emailController.processGatewayRequest(emailRequest)

        then:
        response.getStatusCode() == HttpStatus.OK
        response.getBody().userId == "test-user-id"
        response.getBody().messageHash == "test-hash"
    }
}
