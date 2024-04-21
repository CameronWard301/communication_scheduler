package io.github.cameronward301.communication_scheduler.schedule_api.model;

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
public class CreatePutScheduleDTO {
    private String scheduleId; //only to be sent if updating an exising schedule

    private String gatewayId;
    private String userId;

    private ScheduleCalendarSpecDTO calendar;
    private ScheduleIntervalSpecDTO interval;
    private String cronExpression;

    private boolean paused;

    /**
     * Count the number of specifications provided
     *
     * @return the number of schedule specifications
     */
    public int getNumberOfSpecifications() {
        int count = 0;
        if (calendar != null) count++;
        if (interval != null) count++;
        if (cronExpression != null) count++;
        return count;
    }

    @Getter
    @AllArgsConstructor
    @Builder
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
