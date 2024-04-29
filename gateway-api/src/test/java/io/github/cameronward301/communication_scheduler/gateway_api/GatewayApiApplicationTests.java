package io.github.cameronward301.communication_scheduler.gateway_api;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("default")
class GatewayApiApplicationTests {

    @BeforeAll
    static void setup() {
        System.setProperty("MONGODB_DATABASE_NAME", "test");
        System.setProperty("MONGODB_CONNECTION_STRING", "mongodb://localhost:27017");
    }

    @Test
    void contextLoads() {
    }

}
