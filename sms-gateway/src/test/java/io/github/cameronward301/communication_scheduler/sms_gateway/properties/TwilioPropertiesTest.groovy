package io.github.cameronward301.communication_scheduler.sms_gateway.properties

import spock.lang.Specification

class TwilioPropertiesTest extends Specification {

    def "Should create properties without exception"() {
        when:
        def twilioProperties = new TwilioProperties()
        twilioProperties.setMaximumPollingAttempts(1)
        twilioProperties.setPollingInterval(1)
        twilioProperties.setAccountSid("test-account-sid")
        twilioProperties.setAuthToken("test-auth-token")
        twilioProperties.setFromPhoneNumber("test-from-phone-number")


        then:
        notThrown(Exception)

        and: "Should return correct values"
        twilioProperties.getMaximumPollingAttempts() == 1
        twilioProperties.getPollingInterval() == 1
        twilioProperties.getAccountSid() == "test-account-sid"
        twilioProperties.getAuthToken() == "test-auth-token"
        twilioProperties.getFromPhoneNumber() == "test-from-phone-number"
    }
}
