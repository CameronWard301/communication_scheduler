package io.github.cameronward301.communication_scheduler.mock_gateway.controller;

import io.github.cameronward301.communication_scheduler.gateway_library.controller.GatewayController;
import io.github.cameronward301.communication_scheduler.gateway_library.history.CommunicationHistoryAccessProvider;
import io.github.cameronward301.communication_scheduler.gateway_library.model.GatewayRequest;
import io.github.cameronward301.communication_scheduler.gateway_library.model.GatewayResponse;
import io.github.cameronward301.communication_scheduler.gateway_library.service.CommunicationGatewayService;
import io.github.cameronward301.communication_scheduler.mock_gateway.model.MockContent;
import io.github.cameronward301.communication_scheduler.mock_gateway.model.MockUser;
import io.github.cameronward301.communication_scheduler.mock_gateway.properties.MockGatewayProperties;
import io.github.cameronward301.communication_scheduler.mock_gateway.service.MockDeliveryService;
import io.github.cameronward301.communication_scheduler.mock_gateway.service.MockUserContentService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mock/message")
public class MockGatewayController implements GatewayController<MockUser, MockContent> {

    private final CommunicationGatewayService<MockUser, MockContent> communicationGatewayService;
    private final MockGatewayProperties gatewayProperties;

    public MockGatewayController(CommunicationGatewayService<MockUser, MockContent> communicationGatewayService, CommunicationHistoryAccessProvider communicationHistoryAccessProvider, MockUserContentService mockUserContentService, MockDeliveryService mockDeliveryService) {
        this.communicationGatewayService = communicationGatewayService;
        this.gatewayProperties = MockGatewayProperties.builder()
                .contentDeliveryService(mockDeliveryService)
                .userContentService(mockUserContentService)
                .communicationHistoryAccessProvider(communicationHistoryAccessProvider).build();
    }

    @Override
    @PostMapping("")
    public ResponseEntity<GatewayResponse> processGatewayRequest(GatewayRequest gatewayRequest) {
        return GatewayController.sendCommunication(gatewayRequest, gatewayProperties, communicationGatewayService);
    }
}
