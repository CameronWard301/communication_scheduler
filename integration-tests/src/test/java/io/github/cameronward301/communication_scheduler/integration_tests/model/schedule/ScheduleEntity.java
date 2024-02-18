package io.github.cameronward301.communication_scheduler.integration_tests.model.schedule;

import io.temporal.client.schedules.Schedule;
import io.temporal.client.schedules.ScheduleOptions;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ScheduleEntity {
    private final Schedule schedule;
    private final String scheduleId;
    private final ScheduleOptions scheduleOptions;
}
