package io.github.cameronward301.communication_scheduler.schedule_api.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
public class ModifiedDTO {
    private String message;
    private int totalModified;
}
