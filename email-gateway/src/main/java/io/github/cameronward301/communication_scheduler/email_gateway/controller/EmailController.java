package io.github.cameronward301.communication_scheduler.email_gateway.controller;

import io.github.cameronward301.communication_scheduler.email_gateway.model.EmailContent;
import io.github.cameronward301.communication_scheduler.email_gateway.model.EmailUser;
import io.github.cameronward301.communication_scheduler.email_gateway.properties.EmailGatewayProperties;
import io.github.cameronward301.communication_scheduler.email_gateway.service.EmailMonthlyReportContentDeliveryService;
import io.github.cameronward301.communication_scheduler.email_gateway.service.EmailUserContentService;
import io.github.cameronward301.communication_scheduler.gateway_library.controller.GatewayController;
import io.github.cameronward301.communication_scheduler.gateway_library.history.DefaultCommunicationHistoryAccessProvider;
import io.github.cameronward301.communication_scheduler.gateway_library.model.GatewayRequest;
import io.github.cameronward301.communication_scheduler.gateway_library.model.GatewayResponse;
import io.github.cameronward301.communication_scheduler.gateway_library.service.CommunicationGatewayService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@ComponentScan("io.github.cameronward301.communication_scheduler.email_gateway")
@RequestMapping("/email/monthly-report")
public class EmailController implements GatewayController<EmailUser, EmailContent> {

    private final EmailGatewayProperties emailGatewayProperties;
    private final CommunicationGatewayService<EmailUser, EmailContent> communicationGatewayService;

    public EmailController(EmailMonthlyReportContentDeliveryService contentDeliveryService, EmailUserContentService userContentService, DefaultCommunicationHistoryAccessProvider defaultCommunicationHistoryAccessProvider, CommunicationGatewayService<EmailUser, EmailContent> communicationGatewayService) {
        this.emailGatewayProperties = EmailGatewayProperties.builder()
                .contentDeliveryService(contentDeliveryService)
                .userContentService(userContentService)
                .communicationHistoryAccessProvider(defaultCommunicationHistoryAccessProvider)
                .build();
        this.communicationGatewayService = communicationGatewayService;
    }

    @Override
    @PostMapping("/")
    public ResponseEntity<GatewayResponse> processGatewayRequest(@RequestBody GatewayRequest gatewayRequest) {
        return GatewayController.sendCommunication(gatewayRequest, emailGatewayProperties, communicationGatewayService);
    }
}
