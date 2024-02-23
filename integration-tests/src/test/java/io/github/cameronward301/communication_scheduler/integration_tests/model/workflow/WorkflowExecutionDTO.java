package io.github.cameronward301.communication_scheduler.integration_tests.model.workflow;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowExecutionDTO {
    private String workflowId;
    private String runId;
    private String userId;
    private String gatewayId;
    private String scheduleId;
    private String type;
    private Time startTime;
    private Time endTime;
    private String taskQueue;
    private int status;

    @Getter
    @Builder
    public static class Time {
        private long seconds;
        private long nanos;
    }
}
