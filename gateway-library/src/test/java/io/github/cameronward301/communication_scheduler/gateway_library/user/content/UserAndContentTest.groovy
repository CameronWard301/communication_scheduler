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
}
