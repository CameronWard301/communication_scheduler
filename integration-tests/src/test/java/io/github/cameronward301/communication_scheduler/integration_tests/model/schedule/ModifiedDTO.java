package io.github.cameronward301.communication_scheduler.integration_tests.model.schedule;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ModifiedDTO {
    private String message;
    private int totalModified;
}
