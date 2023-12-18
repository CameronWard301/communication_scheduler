package io.github.cameronward301.communication_scheduler.email_gateway.model

import spock.lang.Specification

class EmailContentTest extends Specification {
    def "setter should set value"() {
        given: "EmailContent"
        EmailContent emailContent = new EmailContent()

        when: "setters are called"
        emailContent.setId("test-content-id")
        emailContent.setUserId("test-user-id")
        emailContent.setMalware(1234)
        emailContent.setAdverts(1111)
        emailContent.setSites(2222)

        then: "values are set"
        emailContent.getMalware() == 1234
        emailContent.getUserId() == "test-user-id"
        emailContent.getAdverts() == 1111
        emailContent.getSites() == 2222
        emailContent.getId() == "test-content-id"
    }

    def "content string should be correct"() {
        given: "EmailContent"
        EmailContent emailContent = new EmailContent()
        emailContent.setMalware(1234)
        emailContent.setUserId("test-user-id")
        emailContent.setAdverts(1111)
        emailContent.setSites(2222)
        emailContent.setId("test-content-id")

        when: "toString is called"
        String contentString = emailContent.getContentString()

        then: "string is correct"
        contentString == "1234 1111 2222"
    }
}
