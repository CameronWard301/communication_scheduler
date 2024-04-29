package io.github.cameronward301.communication_scheduler.email_gateway.properties;

import io.github.cameronward301.communication_scheduler.email_gateway.model.EmailContent;
import io.github.cameronward301.communication_scheduler.email_gateway.model.EmailUser;
import io.github.cameronward301.communication_scheduler.gateway_library.properties.GatewayProperties;
import lombok.experimental.SuperBuilder;

/*
 * Properties for the email gateway, used to configure the generic properties of the gateway
 */
@SuperBuilder
public class EmailGatewayProperties extends GatewayProperties<EmailUser, EmailContent> {

}
