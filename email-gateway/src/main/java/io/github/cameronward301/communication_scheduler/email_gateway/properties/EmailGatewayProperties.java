package io.github.cameronward301.communication_scheduler.email_gateway.properties;

import io.github.cameronward301.communication_scheduler.email_gateway.model.EmailContent;
import io.github.cameronward301.communication_scheduler.email_gateway.model.EmailUser;
import io.github.cameronward301.communication_scheduler.gateway_library.properties.GatewayProperties;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class EmailGatewayProperties extends GatewayProperties<EmailUser, EmailContent> {

}
