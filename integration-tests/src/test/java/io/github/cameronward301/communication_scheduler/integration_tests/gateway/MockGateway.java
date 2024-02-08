package io.github.cameronward301.communication_scheduler.integration_tests.gateway;

import io.cucumber.java.eo.Se;
import io.github.cameronward301.communication_scheduler.integration_tests.properties.GatewayProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class MockGateway extends GatewayType{

    public MockGateway(GatewayProperties gatewayProperties) {
        super(gatewayProperties.getMockGateway().getId());
    }
}
