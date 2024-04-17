package io.github.cameronward301.communication_scheduler.mock_gateway.service;

import io.github.cameronward301.communication_scheduler.gateway_library.service.UserContentService;
import io.github.cameronward301.communication_scheduler.gateway_library.user.content.UserAndContent;
import io.github.cameronward301.communication_scheduler.mock_gateway.model.MockContent;
import io.github.cameronward301.communication_scheduler.mock_gateway.model.MockUser;
import org.springframework.stereotype.Service;

/**
 * Always returns the same message content and user details, pass back the userId that was sent in the request.
 * In a real implementation this would be replaced with a call to a database to get the content
 * for the user and lookup the user by their id to get delivery information such as their email, address or phone number
 */
@Service
public class MockUserContentService implements UserContentService<MockUser, MockContent> {
    @Override
    public UserAndContent<MockUser, MockContent> getUserAndContent(String userId) {
        return UserAndContent.<MockUser, MockContent>builder()
                .user(MockUser.builder()
                        .id(userId)
                        .firstName("Test")
                        .lastName("Name")
                        .build())
                .content(MockContent.builder()
                        .userId(userId)
                        .build())
                .build();
    }
}
