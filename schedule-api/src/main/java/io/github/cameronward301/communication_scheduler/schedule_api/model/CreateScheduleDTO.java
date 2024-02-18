package io.github.cameronward301.communication_scheduler.schedule_api.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;

/**
 * Used to create and update schedules with
 */
@Data
@Builder
public class CreateScheduleDTO {
    private String scheduleId; //only to be sent if updating an exising schedule

    @NotBlank(message = "'gatewayId' cannot be empty")
    private String gatewayId;

    @NotBlank(message = "'userId' cannot be empty")
    private String userId;

    private ScheduleCalendarSpecDTO calendar;
    private ScheduleIntervalSpecDTO interval;
    private String cronExpression;

    private boolean paused;

    /**
     * Only supply one type of way for the schedule to be created
     *
     * @return true if one schedule type has been set otherwise false
     */
    public boolean isInvalid() {
        int count = 0;
        if (calendar != null) count++;
        if (interval != null) count++;
        if (cronExpression != null) count++;
        return count != 1;
    }

    @Getter
    @AllArgsConstructor
    public static class ScheduleCalendarSpecDTO {
        private List<ScheduleRangeDTO> seconds;
        private List<ScheduleRangeDTO> minutes;
        private List<ScheduleRangeDTO> hour;
        private List<ScheduleRangeDTO> dayOfMonth;
        private List<ScheduleRangeDTO> month;
        private List<ScheduleRangeDTO> year;
        private List<ScheduleRangeDTO> dayOfWeek;
    }

    @Getter
    @AllArgsConstructor
    public static class ScheduleRangeDTO {
        private int start;
        private int end;
        private int step;
    }

    @Getter
    @AllArgsConstructor
    public static class ScheduleIntervalSpecDTO {
        private String every;
        private String offset;
    }


}
