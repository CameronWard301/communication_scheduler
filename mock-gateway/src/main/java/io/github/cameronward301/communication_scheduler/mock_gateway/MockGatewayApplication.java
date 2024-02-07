package io.github.cameronward301.communication_scheduler.mock_gateway;

import lombok.Generated;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class MockGatewayApplication {

    @Generated
    public static void main(String[] args) {
        SpringApplication.run(MockGatewayApplication.class, args);
    }

}
