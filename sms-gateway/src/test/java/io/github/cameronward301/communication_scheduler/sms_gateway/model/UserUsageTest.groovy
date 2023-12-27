package io.github.cameronward301.communication_scheduler.sms_gateway.model

import spock.lang.Specification

class UserUsageTest extends Specification {

    def "Should produce correct content string"() {
        given: "User and usage"

        SmsUser user = SmsUser.builder()
                .id("test-id")
                .firstName("test-first-name")
                .phoneNumber("441234567890")
                .build()

        UserUsage userUsage = UserUsage.builder()
                .user(user)
                .sitesVisited(10)
                .malwareBlocked(5)
                .advertsBlocked(15)
                .build()

        when: "Content string is called"
        def contentString = userUsage.getContentString()

        then: "Content string is correct"
        contentString == String.format("%s %s %s %s", userUsage.getUser().getFirstName(), userUsage.getMalwareBlocked(), userUsage.getAdvertsBlocked(), userUsage.getSitesVisited())
    }
}
