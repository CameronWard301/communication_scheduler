package io.github.cameronward301.communication_scheduler.schedule_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/*
 Runs the test profile that creates a different RestTemplateConfigBean
 */
@SpringBootTest
@ActiveProfiles("test")
class PreferencesApiApplicationBeanTests {

    @Test
    void contextLoads() {
    }

}
