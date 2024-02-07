package io.github.cameronward301.communication_scheduler.mock_gateway.service;

import io.github.cameronward301.communication_scheduler.gateway_library.service.UserContentService;
import io.github.cameronward301.communication_scheduler.gateway_library.user.content.UserAndContent;
import io.github.cameronward301.communication_scheduler.mock_gateway.model.MockContent;
import io.github.cameronward301.communication_scheduler.mock_gateway.model.MockUser;
import org.springframework.stereotype.Service;

/**
 * Creates a mock test user from the given
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
