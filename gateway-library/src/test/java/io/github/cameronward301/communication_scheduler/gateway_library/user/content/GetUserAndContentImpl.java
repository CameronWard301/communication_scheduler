package io.github.cameronward301.communication_scheduler.gateway_library.user.content;

import io.github.cameronward301.communication_scheduler.gateway_library.model.TestContent;
import io.github.cameronward301.communication_scheduler.gateway_library.model.User;

public class GetUserAndContentImpl extends GetUserAndContent<User, TestContent> {
    @Override
    protected User getUser(String userId) {
        return User.builder().userId(userId).build();
    }

    @Override
    protected TestContent getContent(String userId) {
        return TestContent.builder().content("test-content").build();
    }
}
