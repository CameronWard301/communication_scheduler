package io.github.cameronward301.communication_scheduler.integration_tests.users;

import lombok.Getter;

@Getter
public abstract class User {
    private final String id;

    public User(String id) {
        this.id = id;
    }

    public User() {
        this.id = "unknown";
    }

}
