package io.github.cameronward301.communication_scheduler.integration_tests.gateway;

import io.github.cameronward301.communication_scheduler.integration_tests.properties.GatewayProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class Email extends GatewayType {

    public Email(GatewayProperties gatewayProperties) {
        super(gatewayProperties.getEmail().getId());
    }
}
