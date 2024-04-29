package io.github.cameronward301.communication_scheduler.email_gateway.service

import com.sendgrid.Request
import com.sendgrid.Response
import com.sendgrid.SendGrid
import io.github.cameronward301.communication_scheduler.email_gateway.model.EmailContent
import io.github.cameronward301.communication_scheduler.email_gateway.model.EmailUser
import io.github.cameronward301.communication_scheduler.email_gateway.properties.SendgridProperties
import io.github.cameronward301.communication_scheduler.gateway_library.exception.ContentDeliveryException
import org.springframework.http.HttpStatus
import spock.lang.Specification

class EmailMonthlyReportContentDeliveryServiceTest extends Specification {
    private SendGrid sendGrid = Mock()
    private SendgridProperties sendgridProperties
    private EmailMonthlyReportContentDeliveryService emailMonthlyReportContentDeliveryService


    def setup() {
        sendgridProperties = new SendgridProperties()
        sendgridProperties.setEmailNewsletterApiKey("test-api-key")
        sendgridProperties.setFromEmail("test-from")
        sendgridProperties.setFromName("test-name")
        sendgridProperties.setMonthlyNewsletterTemplateId("test-template-id")
        emailMonthlyReportContentDeliveryService = new EmailMonthlyReportContentDeliveryService(sendGrid, sendgridProperties)
    }

    def "should send email without exception"() {
        given: "EmailUser and EmailContent"
        EmailUser emailUser = EmailUser.builder()
                .id("test-user-id")
                .email("test@example.com")
                .firstName("test")
                .build()

        EmailContent emailContent = EmailContent.builder()
                .id("test-content-id")
                .malware(1234)
                .userId("test-user-id")
                .adverts(1111)
                .sites(2222)
                .build()

        and: "Response"
        Response response = new Response(HttpStatus.ACCEPTED.value(), "OK", new HashMap<String, String>())


        and: "sendGrid is called with correct parameters and returns response"
        1 * sendGrid.api(_ as Request) >> response


        when: "sendEmail is called"
        emailMonthlyReportContentDeliveryService.sendContent(emailUser, emailContent)


        then: "exception is not thrown"
        notThrown(Exception)
    }

    def "should throw an exception if status is not 200"() {
        given: "EmailUser and EmailContent"
        EmailUser emailUser = EmailUser.builder()
                .id("test-user-id")
                .email("test@example.com")
                .firstName("test")
                .build()

        EmailContent emailContent = EmailContent.builder()
                .id("test-content-id")
                .malware(1234)
                .userId("test-user-id")
                .adverts(1111)
                .sites(2222)
                .build()

        and: "Response"
        Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "OK", new HashMap<String, String>())


        and: "sendGrid is called with correct parameters and returns response"
        1 * sendGrid.api(_ as Request) >> response


        when: "sendEmail is called"
        emailMonthlyReportContentDeliveryService.sendContent(emailUser, emailContent)


        then: "exception is thrown"
        ContentDeliveryException e = thrown(ContentDeliveryException)
        e.getMessage() == "Could not send email to Sendgrid"
    }

    def "should throw an exception if an unknown exception occurs"() {
        given: "EmailUser and EmailContent"
        EmailUser emailUser = EmailUser.builder()
                .id("test-user-id")
                .email("test@example.com")
                .firstName("test")
                .build()

        EmailContent emailContent = EmailContent.builder()
                .id("test-content-id")
                .malware(1234)
                .userId("test-user-id")
                .adverts(1111)
                .sites(2222)
                .build()


        and: "sendGrid is called with correct parameters and returns response"
        1 * sendGrid.api(_ as Request) >> {throw new IOException("test-exception")}


        when: "sendEmail is called"
        emailMonthlyReportContentDeliveryService.sendContent(emailUser, emailContent)


        then: "exception is thrown"
        ContentDeliveryException e = thrown(ContentDeliveryException)
        e.getMessage() == "Could not complete API request to Sendgrid"
    }
}
