package io.github.cameronward301.communication_scheduler.schedule_api.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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


    @Getter
    @AllArgsConstructor
    public static class ScheduleCalendarSpecDTO {
        private List<ScheduleRangeDTO> seconds = new ArrayList<>();
        private List<ScheduleRangeDTO> minutes = new ArrayList<>();
        private List<ScheduleRangeDTO> hour = new ArrayList<>();
        private List<ScheduleRangeDTO> dayOfMonth = new ArrayList<>();
        private List<ScheduleRangeDTO> month = new ArrayList<>();
        private List<ScheduleRangeDTO> year = new ArrayList<>();
        private List<ScheduleRangeDTO> dayOfWeek = new ArrayList<>();
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

    /**
     * Only supply one type of way for the schedule to be created
     * @return true if one schedule type has been set otherwise false
     */
    public boolean isInvalid() {
        int count = 0;
        if (calendar != null) count ++;
        if (interval != null) count ++;
        if (cronExpression != null) count ++;
        return count != 1;
    }


}
