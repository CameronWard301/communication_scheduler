package io.github.cameronward301.communication_scheduler.integration_tests.users;

import io.github.cameronward301.communication_scheduler.integration_tests.properties.GatewayProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class EmailUser1 extends User {
    public EmailUser1(GatewayProperties gatewayProperties) {
        super(gatewayProperties.getEmail().getUser1().getId());
    }
}
