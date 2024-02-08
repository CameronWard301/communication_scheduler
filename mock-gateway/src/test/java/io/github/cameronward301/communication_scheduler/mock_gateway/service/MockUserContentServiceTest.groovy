package io.github.cameronward301.communication_scheduler.mock_gateway.service

import spock.lang.Specification

class MockUserContentServiceTest extends Specification {
    def contentService = new MockUserContentService()

    def "Should return userAndContent"() {
        when:
        def response = contentService.getUserAndContent("test_id")

        then:
        def user = response.getUser()
        user.getId() == "test_id"
        user.getFirstName() == "Test"
        user.getLastName() == "Name"

        and:
        def content = response.getContent()
        content.getContentString() == "test_id HELLO_MOCK_MESSAGE"
    }
}
