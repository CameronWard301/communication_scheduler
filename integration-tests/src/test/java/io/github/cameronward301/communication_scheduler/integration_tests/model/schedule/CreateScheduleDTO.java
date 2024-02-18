package io.github.cameronward301.communication_scheduler.integration_tests.model.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Used to create and update schedules with
 */
@Data
@Builder
public class CreateScheduleDTO {
    private String scheduleId; //only to be sent if updating an exising schedule

    private String gatewayId;

    private String userId;

    private ScheduleCalendarSpecDTO calendar;
    private ScheduleIntervalSpecDTO interval;
    private String cronExpression;

    private boolean paused;

    @AllArgsConstructor
    @Builder
    @Data
    public static class ScheduleCalendarSpecDTO {
        private List<ScheduleRangeDTO> seconds;
        private List<ScheduleRangeDTO> minutes;
        private List<ScheduleRangeDTO> hour;
        private List<ScheduleRangeDTO> dayOfMonth;
        private List<ScheduleRangeDTO> month;
        private List<ScheduleRangeDTO> year;
        private List<ScheduleRangeDTO> dayOfWeek;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class ScheduleRangeDTO {
        private int start;
        private int end;
        private int step;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ScheduleIntervalSpecDTO {
        private String every;
        private String offset;
    }


}
