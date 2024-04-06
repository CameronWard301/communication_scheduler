package io.github.cameronward301.communication_scheduler.schedule_api.helper;

import io.github.cameronward301.communication_scheduler.schedule_api.exception.RequestException;
import io.github.cameronward301.communication_scheduler.schedule_api.model.CreatePutScheduleDTO;
import io.temporal.client.schedules.ScheduleCalendarSpec;
import io.temporal.client.schedules.ScheduleIntervalSpec;
import io.temporal.client.schedules.ScheduleRange;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Converts DTOs to Temporal objects
 */
@Component
public class DtoConverter {

    /**
     * Convert a CalendarSpecDto to a Temporal ScheduleCalendarSpec
     *
     * @param calendarSpecDTO to convert
     * @return Temporal's Schedule Calendar spec
     */
    public ScheduleCalendarSpec getCalendar(CreatePutScheduleDTO.ScheduleCalendarSpecDTO calendarSpecDTO) {
        return ScheduleCalendarSpec.newBuilder()
                .setSeconds(getScheduleRange(calendarSpecDTO.getSeconds()))
                .setHour(getScheduleRange(calendarSpecDTO.getHour()))
                .setMinutes(getScheduleRange(calendarSpecDTO.getMinutes()))
                .setMonth(getScheduleRange(calendarSpecDTO.getMonth()))
                .setYear(getScheduleRange(calendarSpecDTO.getYear()))
                .setDayOfWeek(getScheduleRange(calendarSpecDTO.getDayOfWeek()))
                .setDayOfMonth(getScheduleRange(calendarSpecDTO.getDayOfMonth()))
                .setComment("")
                .build();
    }

    /**
     * Convert an IntervalSpecDTO into a Temporal ScheduleIntervalSpec
     *
     * @param intervalSpecDTO to convert
     * @return ScheduleIntervalSpec
     * @throws RequestException if the string in the DTO cannot be converted to a Duration object
     */
    public ScheduleIntervalSpec getInterval(CreatePutScheduleDTO.ScheduleIntervalSpecDTO intervalSpecDTO) {
        try {
            return new ScheduleIntervalSpec(Duration.parse(intervalSpecDTO.getEvery()), Duration.parse(intervalSpecDTO.getOffset()));

        } catch (DateTimeParseException e) {
            throw new RequestException("Could not parse interval to a datetime object", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * For a given list of rangeDTOs convert them to a list of Temporal Schedule Ranges
     *
     * @param scheduleRangeDTOs to convert
     * @return a list of Temporal ScheduleRange
     */
    private List<ScheduleRange> getScheduleRange(List<CreatePutScheduleDTO.ScheduleRangeDTO> scheduleRangeDTOs) {
        return scheduleRangeDTOs.stream().map(scheduleRangeDTO ->
                        new ScheduleRange(scheduleRangeDTO.getStart(), scheduleRangeDTO.getEnd(), scheduleRangeDTO.getStep()))
                .collect(Collectors.toList());
    }
}
