package io.github.cameronward301.communication_scheduler.gateway_api;

import io.github.cameronward301.communication_scheduler.gateway_api.model.Gateway;
import lombok.Generated;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.repository.RepositoryDefinition;

@SpringBootApplication
@RepositoryDefinition(domainClass = Gateway.class, idClass = String.class)
@Generated
public class GatewayApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApiApplication.class, args);
    }

}
