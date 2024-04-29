package io.github.cameronward301.communication_scheduler.email_gateway.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import io.github.cameronward301.communication_scheduler.email_gateway.model.EmailContent;
import io.github.cameronward301.communication_scheduler.email_gateway.model.EmailUser;
import io.github.cameronward301.communication_scheduler.email_gateway.properties.SendgridProperties;
import io.github.cameronward301.communication_scheduler.gateway_library.exception.ContentDeliveryException;
import io.github.cameronward301.communication_scheduler.gateway_library.service.ContentDeliveryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * This service is responsible for generating the message contents and using the Sendgrid API to send the email
 */
@Service
@Slf4j
public class EmailMonthlyReportContentDeliveryService implements ContentDeliveryService<EmailUser, EmailContent> {
    private final SendGrid sendGrid;
    private final Mail mail = new Mail();
    private final Request request = new Request();


    public EmailMonthlyReportContentDeliveryService(SendGrid sendGrid, SendgridProperties sendgridProperties) {
        this.sendGrid = sendGrid;
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        Email fromEmail = new Email();
        fromEmail.setName(sendgridProperties.getFromName());
        fromEmail.setEmail(sendgridProperties.getFromEmail());
        mail.setFrom(fromEmail);
        mail.setTemplateId(sendgridProperties.getMonthlyNewsletterTemplateId());
    }

    /**
     * Reads the email content and completes the template data for the email
     * Sends the personalised email to the user.
     * @param emailUser    the user to send the content to
     * @param emailContent the content to send to the user
     * @throws ContentDeliveryException if the email could not be sent for any reason
     */
    @Override
    public void sendContent(EmailUser emailUser, EmailContent emailContent) throws ContentDeliveryException {
        Personalization personalization = new Personalization();
        personalization.addDynamicTemplateData("name", emailUser.getFirstName());
        personalization.addDynamicTemplateData("malware", emailContent.getMalware());
        personalization.addDynamicTemplateData("adverts", emailContent.getSites());
        personalization.addDynamicTemplateData("sites", emailContent.getAdverts());
        personalization.addTo(new Email(emailUser.getEmail()));
        personalization.addDynamicTemplateData("subject", "Your Monthly Report Is Here!");
        mail.addPersonalization(personalization);

        try {
            request.setBody(mail.build());
            Response response = sendGrid.api(request);
            if (response.getStatusCode() != 202) {
                log.error("Status: {} Could not send email to Sendgrid: {}", response.getStatusCode(), response.getBody());
                throw new ContentDeliveryException("Could not send email to Sendgrid");
            }
        } catch (IOException e) {
            log.error("Could not send the email: {}", e.getMessage());
            throw new ContentDeliveryException("Could not complete API request to Sendgrid");
        }
    }
}
