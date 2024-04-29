package io.github.cameronward301.communication_scheduler.sms_gateway.controller;

import io.github.cameronward301.communication_scheduler.gateway_library.controller.GatewayController;
import io.github.cameronward301.communication_scheduler.gateway_library.history.DefaultCommunicationHistoryAccessProvider;
import io.github.cameronward301.communication_scheduler.gateway_library.model.GatewayRequest;
import io.github.cameronward301.communication_scheduler.gateway_library.model.GatewayResponse;
import io.github.cameronward301.communication_scheduler.gateway_library.service.CommunicationGatewayService;
import io.github.cameronward301.communication_scheduler.sms_gateway.model.SmsUser;
import io.github.cameronward301.communication_scheduler.sms_gateway.model.UserUsage;
import io.github.cameronward301.communication_scheduler.sms_gateway.properties.SmsGatewayProperties;
import io.github.cameronward301.communication_scheduler.sms_gateway.service.SmsUserContentService;
import io.github.cameronward301.communication_scheduler.sms_gateway.service.SmsWeeklyReportDeliveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for handling SMS gateway requests
 * I recommend a separate controller for each time a different content delivery service is used
 */
@Controller
@RequestMapping("/sms/weekly-report")
public class SmsController implements GatewayController<SmsUser, UserUsage> {

    private final SmsGatewayProperties smsGatewayProperties;
    private final CommunicationGatewayService<SmsUser, UserUsage> communicationGatewayService;

    public SmsController(CommunicationGatewayService<SmsUser, UserUsage> communicationGatewayService, DefaultCommunicationHistoryAccessProvider accessProvider, SmsWeeklyReportDeliveryService deliveryService, SmsUserContentService userContentService) {
        this.communicationGatewayService = communicationGatewayService;
        smsGatewayProperties = SmsGatewayProperties.builder()
                .communicationHistoryAccessProvider(accessProvider)
                .contentDeliveryService(deliveryService)
                .userContentService(userContentService)
                .build();
    }

    @Override
    @PostMapping("/")
    public ResponseEntity<GatewayResponse> processGatewayRequest(GatewayRequest gatewayRequest) {
        return GatewayController.sendCommunication(gatewayRequest, smsGatewayProperties, communicationGatewayService);
    }
}
