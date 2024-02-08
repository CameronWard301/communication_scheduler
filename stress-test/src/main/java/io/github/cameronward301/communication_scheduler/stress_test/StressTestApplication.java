package io.github.cameronward301.communication_scheduler.stress_test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.task.configuration.EnableTask;

@SpringBootApplication(exclude =  {DataSourceAutoConfiguration.class })
@EnableTask
public class StressTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(StressTestApplication.class, args);
    }

}
