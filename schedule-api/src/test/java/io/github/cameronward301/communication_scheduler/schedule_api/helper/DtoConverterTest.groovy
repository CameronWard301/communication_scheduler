package io.github.cameronward301.communication_scheduler.schedule_api.helper

import io.github.cameronward301.communication_scheduler.schedule_api.exception.RequestException
import io.github.cameronward301.communication_scheduler.schedule_api.model.CreatePutScheduleDTO
import org.springframework.http.HttpStatus
import spock.lang.Specification

class DtoConverterTest extends Specification {
    def dtoConverter = new DtoConverter()

    def "Should throw exception if interval cannot be parsed into a Duration"() {
        given:
        def intervalSpec = new CreatePutScheduleDTO.ScheduleIntervalSpecDTO("a", "b")

        when:
        dtoConverter.getInterval(intervalSpec)

        then:
        def exception = thrown(RequestException)
        exception.getHttpStatus() == HttpStatus.BAD_REQUEST
        exception.getMessage() == "Could not parse interval to a datetime object"
    }
}
