package io.github.cameronward301.communication_scheduler.schedule_api.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UpdateDTO {
    private String message;
    private int totalUpdated;
}
