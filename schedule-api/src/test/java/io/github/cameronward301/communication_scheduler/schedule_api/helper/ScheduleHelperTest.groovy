package io.github.cameronward301.communication_scheduler.schedule_api.helper

import io.github.cameronward301.communication_scheduler.schedule_api.exception.RequestException
import io.github.cameronward301.communication_scheduler.schedule_api.model.CreateScheduleDTO
import org.springframework.http.HttpStatus
import spock.lang.Specification

class ScheduleHelperTest extends Specification {
    def dtoConverter = new DtoConverter()
    def helper = new ScheduleHelper("test-queue", dtoConverter)

    def "Should get schedule spec with calendar"() {
        given:
        def scheduleDto = CreateScheduleDTO.builder()
                .scheduleId("123")
                .gatewayId("1234")
                .userId("12345")
                .calendar(new CreateScheduleDTO.ScheduleCalendarSpecDTO(
                        [new CreateScheduleDTO.ScheduleRangeDTO(1, 1, 0)],
                        [new CreateScheduleDTO.ScheduleRangeDTO(1, 1, 0)],
                        [new CreateScheduleDTO.ScheduleRangeDTO(1, 1, 0)],
                        [new CreateScheduleDTO.ScheduleRangeDTO(1, 1, 0)],
                        [new CreateScheduleDTO.ScheduleRangeDTO(1, 1, 0)],
                        [new CreateScheduleDTO.ScheduleRangeDTO(1, 1, 0)],
                        [new CreateScheduleDTO.ScheduleRangeDTO(1, 1, 0)]
                )).build()

        when:
        def result = helper.getScheduleSpec(scheduleDto)

        then:
        result.getCalendars() != null
        result.getCalendars().get(0) == dtoConverter.getCalendar(scheduleDto.getCalendar())
        result.getCalendars().get(0).getYear().get(0).getStart() == 1
    }

    def "Should get schedule spec with interval"() {
        given:
        def scheduleDto = CreateScheduleDTO.builder()
                .scheduleId("123")
                .gatewayId("1234")
                .userId("12345")
                .interval(new CreateScheduleDTO.ScheduleIntervalSpecDTO("PT1S", "PT0S"))
                .build()

        when:
        def result = helper.getScheduleSpec(scheduleDto)

        then:
        result.getIntervals() != null
        result.getIntervals().get(0) == dtoConverter.getInterval(scheduleDto.getInterval())
    }

    def "Should throw exception if no schedule spec provided"() {
        given:
        def scheduleDto = CreateScheduleDTO.builder().build()

        when:
        helper.getScheduleSpec(scheduleDto)

        then:
        def exception = thrown(RequestException)
        exception.getMessage() == "Please provide exactly one schedule configuration, either: 'calendar', 'interval' or 'cronExpression'"
        exception.getHttpStatus() == HttpStatus.BAD_REQUEST
    }
}
