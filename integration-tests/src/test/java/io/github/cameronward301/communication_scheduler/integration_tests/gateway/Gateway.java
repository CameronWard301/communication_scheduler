package io.github.cameronward301.communication_scheduler.integration_tests.gateway;


import lombok.Getter;

@Getter
public abstract class Gateway {
    private final String id;

    public Gateway(String id) {
        this.id = id;
    }

    public Gateway() {
        this.id = "unknown";
    }
}
