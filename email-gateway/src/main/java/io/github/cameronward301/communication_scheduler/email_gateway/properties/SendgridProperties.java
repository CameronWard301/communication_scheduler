package io.github.cameronward301.communication_scheduler.email_gateway.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Properties for the Sendgrid email gateway read from the application.yml file
 */
@Configuration
@ConfigurationProperties(prefix = "sendgrid")
@Getter
@Setter
public class SendgridProperties {
    private String emailNewsletterApiKey;
    private String monthlyNewsletterTemplateId;
    private String fromName;
    private String fromEmail;
}
