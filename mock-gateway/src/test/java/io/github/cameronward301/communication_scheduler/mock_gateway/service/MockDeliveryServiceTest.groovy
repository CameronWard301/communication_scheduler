package io.github.cameronward301.communication_scheduler.mock_gateway.service

import io.github.cameronward301.communication_scheduler.mock_gateway.model.MockContent
import io.github.cameronward301.communication_scheduler.mock_gateway.model.MockUser
import spock.lang.Specification

class MockDeliveryServiceTest extends Specification {

    def mockDeliveryService = new MockDeliveryService(1)

    def "Should send content without exception"() {
        given: "user and content"
        def mockUser = MockUser.builder()
                .firstName("first")
                .lastName("last")
                .id("test")
        .build()

        def mockContent = MockContent.builder()
            .userId(mockUser.getId())
            .build()

        when:
        mockDeliveryService.sendContent(mockUser, mockContent)

        then:
        notThrown(Exception)
    }
}
