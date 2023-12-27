package io.github.cameronward301.communication_scheduler.sms_gateway.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.twilio.rest.api.v2010.account.Message
import io.github.cameronward301.communication_scheduler.gateway_library.exception.ContentDeliveryException
import io.github.cameronward301.communication_scheduler.sms_gateway.model.SmsUser
import io.github.cameronward301.communication_scheduler.sms_gateway.model.UserUsage
import io.github.cameronward301.communication_scheduler.sms_gateway.properties.TwilioProperties
import spock.lang.Specification

class SmsWeeklyReportDeliveryServiceTest extends Specification {

    def objectMapper = new ObjectMapper()

    def twilioService = Mock(TwilioService)

    def twilioProperties = new TwilioProperties()

    SmsWeeklyReportDeliveryService smsWeeklyReportDeliveryService

    def setup() {
        twilioProperties.setAccountSid("test-account-sid")
        twilioProperties.setAuthToken("test-auth-token")
        twilioProperties.setFromPhoneNumber("test-from-phone-number")
        twilioProperties.setPollingInterval(1)
        twilioProperties.setMaximumPollingAttempts(1)
    }

    def "Should send sms without exception"() {
        given: "delivery service"
        smsWeeklyReportDeliveryService = new SmsWeeklyReportDeliveryService(twilioProperties, twilioService)

        and: "user and user usage"
        SmsUser smsUser = SmsUser.builder()
                .id("test-user-id")
                .phoneNumber("441234567890")
                .firstName("test")
                .build()

        UserUsage userUsage = UserUsage.builder()
                .id("test-usage-id")
                .user(smsUser)
                .malwareBlocked(1234)
                .advertsBlocked(1111)
                .sitesVisited(2222)
                .build()

        and: "Message queued is"
        Message messageQueued = createMessageFromJson("test-from-phone-number", "441234567890", "queued", objectMapper)

        and: "Message queried is"
        Message messageQueried = createMessageFromJson("test-from-phone-number", "441234567890", "delivered", objectMapper)
        when:
        smsWeeklyReportDeliveryService.sendContent(smsUser, userUsage)

        then:
        1 * twilioService.sendSms(smsUser.getPhoneNumber(), _ as String) >> messageQueued

        and:
        1 * twilioService.getMessageBySid("test-message-sid") >> messageQueried

        and:
        notThrown(Exception)
    }

    def "should throw exception if message status is error"() {
        given: "delivery service"
        smsWeeklyReportDeliveryService = new SmsWeeklyReportDeliveryService(twilioProperties, twilioService)

        and: "user and user usage"
        SmsUser smsUser = SmsUser.builder()
                .id("test-user-id")
                .phoneNumber("441234567890")
                .firstName("test")
                .build()

        UserUsage userUsage = UserUsage.builder()
                .id("test-usage-id")
                .user(smsUser)
                .malwareBlocked(1234)
                .advertsBlocked(1111)
                .sitesVisited(2222)
                .build()

        and: "Message queued is"
        Message messageQueued = createMessageFromJson("test-from-phone-number", "441234567890", "queued", objectMapper)

        and: "Message queried is"
        Message messageQueried = createMessageFromJson("test-from-phone-number", "441234567890", "failed", "test-error", objectMapper)

        when:
        smsWeeklyReportDeliveryService.sendContent(smsUser, userUsage)

        then:
        1 * twilioService.sendSms(smsUser.getPhoneNumber(), _ as String) >> messageQueued

        and:
        1 * twilioService.getMessageBySid("test-message-sid") >> messageQueried

        and:
        ContentDeliveryException e = thrown(ContentDeliveryException)
        e.getMessage() == "Message failed to send: test-error"
    }

    def "should poll again if status hasn't changed"() {
        given: "delivery service"
        twilioProperties.setMaximumPollingAttempts(2)
        smsWeeklyReportDeliveryService = new SmsWeeklyReportDeliveryService(twilioProperties, twilioService)

        and: "user and user usage"
        SmsUser smsUser = SmsUser.builder()
                .id("test-user-id")
                .phoneNumber("441234567890")
                .firstName("test")
                .build()

        UserUsage userUsage = UserUsage.builder()
                .id("test-usage-id")
                .user(smsUser)
                .malwareBlocked(1234)
                .advertsBlocked(1111)
                .sitesVisited(2222)
                .build()

        and: "Message queued is"
        Message messageQueued = createMessageFromJson("test-from-phone-number", "441234567890", "queued", objectMapper)

        and: "Message delivered is"
        Message messageDelivered = createMessageFromJson("test-from-phone-number", "441234567890", "delivered", objectMapper)

        when:
        smsWeeklyReportDeliveryService.sendContent(smsUser, userUsage)

        then: "Message is queued"
        1 * twilioService.sendSms(smsUser.getPhoneNumber(), _ as String) >> messageQueued

        and: "Message is queried once and is still queued"
        1 * twilioService.getMessageBySid("test-message-sid") >> messageQueued

        and: "Message is queried again and is delivered"
        1 * twilioService.getMessageBySid("test-message-sid") >> messageDelivered

        and:
        notThrown(Exception)

    }

    def "should throw exception if too many queries"() {
        given: "delivery service"
        smsWeeklyReportDeliveryService = new SmsWeeklyReportDeliveryService(twilioProperties, twilioService)

        and: "user and user usage"
        SmsUser smsUser = SmsUser.builder()
                .id("test-user-id")
                .phoneNumber("441234567890")
                .firstName("test")
                .build()

        UserUsage userUsage = UserUsage.builder()
                .id("test-usage-id")
                .user(smsUser)
                .malwareBlocked(1234)
                .advertsBlocked(1111)
                .sitesVisited(2222)
                .build()

        and: "Message queued is"
        Message messageQueued = createMessageFromJson("test-from-phone-number", "441234567890", "queued", objectMapper)


        when:
        smsWeeklyReportDeliveryService.sendContent(smsUser, userUsage)

        then: "Message is queued"
        1 * twilioService.sendSms(smsUser.getPhoneNumber(), _ as String) >> messageQueued

        and: "Message is queried once and is still queued"
        1 * twilioService.getMessageBySid("test-message-sid") >> messageQueued

        and:
        ContentDeliveryException e = thrown(ContentDeliveryException)
        e.getMessage() == "Could not resolve message status after 1 attempts"

    }

    def "should throw exception if thread is interrupted"() {
        given: "delivery service"
        smsWeeklyReportDeliveryService = new SmsWeeklyReportDeliveryService(twilioProperties, twilioService)

        and: "user and user usage"
        SmsUser smsUser = SmsUser.builder()
                .id("test-user-id")
                .phoneNumber("441234567890")
                .firstName("test")
                .build()

        UserUsage userUsage = UserUsage.builder()
                .id("test-usage-id")
                .user(smsUser)
                .malwareBlocked(1234)
                .advertsBlocked(1111)
                .sitesVisited(2222)
                .build()

        and: "Message queued is"
        Message messageQueued = createMessageFromJson("test-from-phone-number", "441234567890", "queued", objectMapper)

        when: "thread is interrupted"
        Thread.currentThread().interrupt()
        smsWeeklyReportDeliveryService.sendContent(smsUser, userUsage)

        then: "Message is queued"
        1 * twilioService.sendSms(smsUser.getPhoneNumber(), _ as String) >> messageQueued

        and: "Message is queried once and is still queued"
        1 * twilioService.getMessageBySid("test-message-sid") >> messageQueued

        and: "exception is thrown"
        ContentDeliveryException e = thrown(ContentDeliveryException)
        e.getMessage() == "Interrupted while waiting for message status"
    }

    def createMessageFromJson(String from, String to, String status, ObjectMapper objectMapper) {
        createMessageFromJson(from, to, status, "null", objectMapper)
    }

    def createMessageFromJson(String from, String to, String status, String errorMessage, ObjectMapper objectMapper) {
        String json = String.format("""
            {
              "account_sid": "test-account-sid",
              "api_version": "2010-04-01",
              "body": "Hi there",
              "date_created": "Thu, 24 Aug 2023 05:01:45 +0000",
              "date_sent": "Thu, 24 Aug 2023 05:01:45 +0000",
              "date_updated": "Thu, 24 Aug 2023 05:01:45 +0000",
              "direction": "outbound-api",
              "error_code": null,
              "error_message": "%s",
              "from": "%s",
              "num_media": "0",
              "num_segments": "1",
              "price": null,
              "price_unit": null,
              "messaging_service_sid": "test-message-service-sid",
              "sid": "test-message-sid",
              "status": "%s",
              "subresource_uris": {
                "media": "/2010-04-01/Accounts/test-account-sid/Messages/test-message-sid/Media.json"
              },
              "to": "%s",
              "uri": "/2010-04-01/Accounts/test-account-sid/Messages/test-message-sid.json"
            }
        """, errorMessage, from, status, to)

        return Message.fromJson(json, objectMapper)
    }
}
