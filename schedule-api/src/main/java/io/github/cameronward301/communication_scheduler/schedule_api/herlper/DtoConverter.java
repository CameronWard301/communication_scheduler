package io.github.cameronward301.communication_scheduler.schedule_api.herlper;

import io.github.cameronward301.communication_scheduler.schedule_api.exception.RequestException;
import io.github.cameronward301.communication_scheduler.schedule_api.model.CreateScheduleDTO;
import io.temporal.client.schedules.ScheduleCalendarSpec;
import io.temporal.client.schedules.ScheduleIntervalSpec;
import io.temporal.client.schedules.ScheduleRange;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DtoConverter {
    public ScheduleCalendarSpec getCalendar(CreateScheduleDTO.ScheduleCalendarSpecDTO calendarSpecDTO) {
        return ScheduleCalendarSpec.newBuilder()
                .setSeconds(getScheduleRange(calendarSpecDTO.getSeconds()))
                .setHour(getScheduleRange(calendarSpecDTO.getHour()))
                .setMinutes(getScheduleRange(calendarSpecDTO.getMinutes()))
                .setMonth(getScheduleRange(calendarSpecDTO.getMonth()))
                .setDayOfWeek(getScheduleRange(calendarSpecDTO.getDayOfWeek()))
                .setDayOfMonth(getScheduleRange(calendarSpecDTO.getDayOfMonth()))
                .setComment("")
                .build();
    }

    public ScheduleIntervalSpec getInterval(CreateScheduleDTO.ScheduleIntervalSpecDTO intervalSpecDTO) {
        try {
            return new ScheduleIntervalSpec(Duration.parse(intervalSpecDTO.getEvery()), Duration.parse(intervalSpecDTO.getOffset()));

        } catch (DateTimeParseException e) {
            throw new RequestException("Could not parse interval to a datetime object", HttpStatus.BAD_REQUEST);
        }
    }

    private List<ScheduleRange> getScheduleRange(List<CreateScheduleDTO.ScheduleRangeDTO> scheduleRangeDTOs) {
        return scheduleRangeDTOs.stream().map(scheduleRangeDTO ->
                        new ScheduleRange(scheduleRangeDTO.getStart(), scheduleRangeDTO.getEnd(), scheduleRangeDTO.getStep()))
                .collect(Collectors.toList());
    }
}
