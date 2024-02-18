package io.github.cameronward301.communication_scheduler.integration_tests.model.schedule;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SchedulePatchDTO {
    private Boolean paused;
    private String gatewayId;
}
