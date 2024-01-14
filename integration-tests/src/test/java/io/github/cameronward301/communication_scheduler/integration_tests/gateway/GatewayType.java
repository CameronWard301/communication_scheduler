package io.github.cameronward301.communication_scheduler.integration_tests.gateway;


import lombok.Getter;

@Getter
public abstract class GatewayType {
    private final String id;

    public GatewayType(String id) {
        this.id = id;
    }

    public GatewayType() {
        this.id = "unknown";
    }
}
