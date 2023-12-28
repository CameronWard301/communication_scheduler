package io.github.cameronward301.communication_scheduler.gateway_library.user.content;

import io.github.cameronward301.communication_scheduler.gateway_library.model.TestContent;
import io.github.cameronward301.communication_scheduler.gateway_library.model.User;
import io.github.cameronward301.communication_scheduler.gateway_library.service.UserContentService;

public class GetUserWithContentImpl implements UserContentService<User, TestContent> {

    @Override
    public UserAndContent<User, TestContent> getUserAndContent(String userId) {
        return UserAndContent.<User, TestContent>builder()
                .content(TestContent.builder()
                        .content("test-content")
                        .build())
                .user(User.builder()
                        .userId(userId)
                        .build())
                .build();
    }
}
