package io.github.cameronward301.communication_scheduler.mock_gateway.model

import spock.lang.Specification

class MockUserTest extends Specification {
    def "Should create mock user"() {
        when:
        def user = new MockUser()
        user.setFirstName("first")
        user.setLastName("second")
        user.setId("my_id")

        then:
        user.getFirstName() == "first"
        user.getLastName() == "second"
        user.setId("my_id")
    }

}
