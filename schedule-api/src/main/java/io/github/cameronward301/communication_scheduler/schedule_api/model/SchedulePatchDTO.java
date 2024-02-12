package io.github.cameronward301.communication_scheduler.schedule_api.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SchedulePatchDTO {
    @NotBlank (message = "'paused' must not be empty")
    private boolean paused;

    @NotBlank (message = "'gatewayId' must not be empty")
    private String gatewayId;
}
