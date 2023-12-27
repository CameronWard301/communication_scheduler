package io.github.cameronward301.communication_scheduler.sms_gateway.service

import io.github.cameronward301.communication_scheduler.sms_gateway.properties.TwilioProperties
import spock.lang.Specification

class TwilioServiceTest extends Specification {

    def "Should create service without exception"() {
        given:
        TwilioProperties twilioProperties = new TwilioProperties()
        twilioProperties.authToken = "test-auth-token"
        twilioProperties.accountSid = "test-account-sid"

        when:
        new TwilioService(twilioProperties)


        then:
        notThrown(Exception)
    }
}
