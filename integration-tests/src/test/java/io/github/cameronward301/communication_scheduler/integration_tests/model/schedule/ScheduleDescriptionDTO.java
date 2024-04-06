package io.github.cameronward301.communication_scheduler.integration_tests.model.schedule;

import io.temporal.client.schedules.ScheduleCalendarSpec;
import io.temporal.client.schedules.ScheduleInfo;
import io.temporal.client.schedules.ScheduleIntervalSpec;
import io.temporal.client.schedules.ScheduleState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleDescriptionDTO {
    private String scheduleId;
    private ScheduleInfo info;
    private ScheduleDTO schedule;
    private Map<String, List<?>> searchAttributes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScheduleDTO {
        private ScheduleState state;
        private ScheduleSpecDTO spec;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScheduleSpecDTO {
        private List<CreateScheduleDTO.ScheduleCalendarSpecDTO> calendars;
        private List<ScheduleIntervalSpec> intervals;
        private List<String> cronExpressions;
        private List<ScheduleCalendarSpec> skip;
        private Instant startAt;
        private Instant endAt;
        private Duration jitter;
        private String timeZoneName;
    }
}
