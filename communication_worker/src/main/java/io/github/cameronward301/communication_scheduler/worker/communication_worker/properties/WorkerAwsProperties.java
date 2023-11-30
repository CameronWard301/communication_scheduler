package io.github.cameronward301.communication_scheduler.worker.communication_worker.properties;


import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.properties.AwsProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties("aws-properties")
@Getter
@Setter
public class WorkerAwsProperties extends AwsProperties {
    private String region;
}
