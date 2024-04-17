package io.github.cameronward301.communication_scheduler.sms_gateway.properties;

import io.github.cameronward301.communication_scheduler.gateway_library.properties.GatewayProperties;
import io.github.cameronward301.communication_scheduler.sms_gateway.model.SmsUser;
import io.github.cameronward301.communication_scheduler.sms_gateway.model.UserUsage;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Creates the properties for the SMS gateway and used to configure the generics
 */
@Getter
@Setter
@SuperBuilder
public class SmsGatewayProperties extends GatewayProperties<SmsUser, UserUsage> {
}
