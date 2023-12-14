package io.github.cameronward301.communication_scheduler.gateway_library.controller;

import io.github.cameronward301.communication_scheduler.gateway_library.model.GatewayRequest;
import io.github.cameronward301.communication_scheduler.gateway_library.model.GatewayResponse;
import io.github.cameronward301.communication_scheduler.gateway_library.model.TestContent;
import io.github.cameronward301.communication_scheduler.gateway_library.model.User;
import io.github.cameronward301.communication_scheduler.gateway_library.properties.GatewayProperties;
import io.github.cameronward301.communication_scheduler.gateway_library.service.CommunicationGatewayService;
import org.springframework.http.ResponseEntity;

public class GatewayControllerImpl implements GatewayController<User, TestContent> {

    private final GatewayProperties<User, TestContent> gatewayProperties;
    private final CommunicationGatewayService<User, TestContent> communicationGatewayService;

    public GatewayControllerImpl(GatewayProperties<User, TestContent> gatewayProperties, CommunicationGatewayService<User, TestContent> communicationGatewayService) {
        this.gatewayProperties = gatewayProperties;
        this.communicationGatewayService = communicationGatewayService;
    }

    @Override
    public ResponseEntity<GatewayResponse> processGatewayRequest(GatewayRequest gatewayRequest) {
        return GatewayController.sendCommunication(gatewayRequest, gatewayProperties, communicationGatewayService);
    }

}
