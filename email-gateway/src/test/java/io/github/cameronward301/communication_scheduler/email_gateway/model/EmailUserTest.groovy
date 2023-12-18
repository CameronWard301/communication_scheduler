package io.github.cameronward301.communication_scheduler.email_gateway.model

import spock.lang.Specification

class EmailUserTest extends Specification {
    def "setter should set value"() {
        given: "EmailUser"
        EmailUser emailUser = new EmailUser()

        when: "setters are called"
        emailUser.setId("test-user-id")
        emailUser.setEmail("test@example.com")
        emailUser.setFirstName("test")

        then: "values are set"
        emailUser.getId() == "test-user-id"
        emailUser.getEmail() == "test@example.com"
        emailUser.getFirstName() == "test"
    }
}
