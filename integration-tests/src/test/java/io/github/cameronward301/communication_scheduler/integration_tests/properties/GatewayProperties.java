package io.github.cameronward301.communication_scheduler.integration_tests.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "gateway")
@Data
public class GatewayProperties {
    private GatewayProperties.Sms sms;
    private GatewayProperties.Email email;
    private GatewayProperties.MockGateway mockGateway;

    @Data
    public static class Sms {
        private String id;
        private GatewayProperties.User user1;
    }

    @Data
    public static class Email {
        private String id;
        private GatewayProperties.User user1;
    }

    @Data
    public static class User {
        private String id;
    }

    @Data
    public static class MockGateway {
        private String id;
    }
}
