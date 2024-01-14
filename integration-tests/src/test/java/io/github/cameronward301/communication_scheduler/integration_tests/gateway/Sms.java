package io.github.cameronward301.communication_scheduler.integration_tests.gateway;

import io.github.cameronward301.communication_scheduler.integration_tests.properties.GatewayProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class Sms extends GatewayType {

    public Sms(GatewayProperties gatewayProperties) {
        super(gatewayProperties.getSms().getId());
    }
}
