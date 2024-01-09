package io.github.cameronward301.communication_scheduler.integration_tests.users;

import io.github.cameronward301.communication_scheduler.integration_tests.properties.GatewayProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class SmsUser1 extends User {


    public SmsUser1(GatewayProperties gatewayProperties) {
        super(gatewayProperties.getSms().getUser1().getId());
    }
}
