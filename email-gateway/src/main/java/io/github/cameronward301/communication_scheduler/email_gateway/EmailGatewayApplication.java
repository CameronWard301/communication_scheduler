package io.github.cameronward301.communication_scheduler.email_gateway;

import io.github.cameronward301.communication_scheduler.gateway_library.configuration.SharedGatewayConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
public class EmailGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmailGatewayApplication.class, args);
    }

}
