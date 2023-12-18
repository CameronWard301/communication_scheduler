package io.github.cameronward301.communication_scheduler.gateway_library.user.content;

import io.github.cameronward301.communication_scheduler.gateway_library.model.TestContent;
import io.github.cameronward301.communication_scheduler.gateway_library.model.User;

public class GetUserWithContentImpl extends GetUserWithContent<User, TestContent> {

    @Override
    protected UserAndContent<User, TestContent> getUserWithContent(String userId) {
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
