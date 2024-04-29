package io.github.cameronward301.communication_scheduler.email_gateway.configuration;

import com.sendgrid.SendGrid;
import io.github.cameronward301.communication_scheduler.email_gateway.properties.SendgridProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Creates a sendgrid bean for sending emails
 */
@AutoConfiguration
public class EmailGatewayConfiguration {

    private final SendgridProperties sendgridProperties;

    public EmailGatewayConfiguration(SendgridProperties sendgridProperties) {
        this.sendgridProperties = sendgridProperties;
    }

    @Bean
    public SendGrid sendGrid() {
        return new SendGrid(sendgridProperties.getEmailNewsletterApiKey());
    }
}
