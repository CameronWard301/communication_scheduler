package io.github.cameronward301.communication_scheduler.gateway_library.user.content

import io.github.cameronward301.communication_scheduler.gateway_library.model.TestContent
import io.github.cameronward301.communication_scheduler.gateway_library.model.User
import spock.lang.Specification

class UserAndContentTest extends Specification {

    def "should set user and content"() {
        given:
        TestContent content = Mock(TestContent)
        User user = Mock(User)
        def userAndContent = UserAndContent.builder().build()

        when: "using setters"
        userAndContent.setContent(content)
        userAndContent.setUser(user)

        then: "values are set"
        userAndContent.getContent() == content
        userAndContent.getUser() == user
    }

    def "Should Get User And Content"(){
        given: "User and Content"
        GetUserAndContentImpl getUserAndContent = new GetUserAndContentImpl()

        and: "userId and content"
        String userId = "test-user-id"
        String content = "test-content"

        when: "Get User and Content is called"
        User user = getUserAndContent.getUser(userId)
        TestContent testContent = getUserAndContent.getContent(content)

        then: "User and Content are returned"
        userId == user.getUserId()
        content == testContent.getContent()
    }

    def "Should get user with content"() {
        given: "User With Content"
        GetUserWithContentImpl getUserWithContent = new GetUserWithContentImpl()

        and: "userId and content"
        String userId = "test-user-id"
        String content = "test-content"

        when: "Get User and Content is called"
        UserAndContent userAndContent = getUserWithContent.getUserAndContent(userId)

        then: "User and Content are returned"
        userId == userAndContent.getUser().getUserId()
        content == userAndContent.getContent().getContent()
    }
}
