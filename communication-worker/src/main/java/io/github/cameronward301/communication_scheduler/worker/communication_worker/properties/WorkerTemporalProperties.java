package io.github.cameronward301.communication_scheduler.worker.communication_worker.properties;

import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.properties.TemporalProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("temporal-properties")
public class WorkerTemporalProperties extends TemporalProperties {
    private String taskQueue;
    private String endpoint;
}
