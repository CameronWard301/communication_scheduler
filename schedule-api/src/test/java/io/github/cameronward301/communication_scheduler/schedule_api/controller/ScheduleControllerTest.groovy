package io.github.cameronward301.communication_scheduler.schedule_api.controller

import io.github.cameronward301.communication_scheduler.schedule_api.controler.ScheduleController
import io.github.cameronward301.communication_scheduler.schedule_api.exception.RequestException
import io.github.cameronward301.communication_scheduler.schedule_api.model.*
import io.github.cameronward301.communication_scheduler.schedule_api.service.ScheduleService
import io.temporal.api.common.v1.Payload
import io.temporal.client.schedules.*
import io.temporal.common.converter.DefaultDataConverter
import org.springframework.data.domain.PageImpl
import org.springframework.http.HttpStatus
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import spock.lang.Specification

import java.time.Instant

class ScheduleControllerTest extends Specification {
    private ScheduleController scheduleController
    private ScheduleService service = Mock(ScheduleService)

    def setup() {
        scheduleController = new ScheduleController(service)
    }

    def "Should return 200 when getSchedules is called"() {
        given: "page, user and gateway parameters"
        def pageNumber = "2"
        def pageSize = "1"
        def userId = Optional.of("1234")
        def gatewayId = Optional.of("4567")
        def scheduleId = "abc"

        and:
        def schedule = new ScheduleListDescription(
                scheduleId,
                new ScheduleListSchedule(
                        new ScheduleListActionStartWorkflow("123"),
                        ScheduleSpec.newBuilder().build(),
                        new ScheduleListState("", false)
                ),
                new ScheduleListInfo([], []),
                Map as Map<String, Payload>,
                DefaultDataConverter.newDefaultInstance(),
                Map.of("a", "b")
        )

        and:
        service.getAllSchedules(pageNumber, pageSize, userId, gatewayId) >> new PageImpl<>(List.of(schedule))

        when:
        def response = scheduleController.getSchedules(pageNumber, pageSize, userId, gatewayId)

        then:
        response.getStatusCode() == HttpStatus.OK
        response.getBody().getContent().get(0) == schedule
    }

    def "Should return 200 and return created schedule"() {
        given: "Create Request"
        def request = CreatePutScheduleDTO.builder()
                .gatewayId("123")
                .userId("2134")
                .interval(new CreatePutScheduleDTO.ScheduleIntervalSpecDTO("PT10S", "PT0S"))
                .build()

        and: "Binding result has no error"
        def bindingResult = Mock(BindingResult)
        bindingResult.hasErrors() >> false

        and: "Service returns created schedule"
        service.createSchedule(request) >> new ScheduleDescriptionDTO()

        when:
        def response = scheduleController.createSchedule(request, bindingResult)

        then:
        response.getStatusCode() == HttpStatus.CREATED
    }

    def "Should throw Request Exception with status 400 if there are binding errors when creating"() {
        given: "Create Request"
        def request = CreatePutScheduleDTO.builder()
                .gatewayId("123")
                .userId("2134")
                .interval(new CreatePutScheduleDTO.ScheduleIntervalSpecDTO("PT10S", "PT0S"))
                .build()

        and: "Binding result has errors"
        def bindingResult = Mock(BindingResult)
        bindingResult.hasErrors() >> true
        FieldError fieldError = new FieldError("test", "test", "test")
        bindingResult.getFieldError() >> fieldError

        when:
        scheduleController.createSchedule(request, bindingResult)

        then:
        def exception = thrown(RequestException)
        exception.getHttpStatus() == HttpStatus.BAD_REQUEST
    }

    def "Should throw Request Exception with status 400 if there gatewayId is not present"() {
        given: "Create Request"
        def request = CreatePutScheduleDTO.builder()
                .userId("2134")
                .cronExpression("1234")
                .build()

        and: "Binding result has no errors"
        def bindingResult = Mock(BindingResult)
        bindingResult.hasErrors() >> false

        when:
        scheduleController.createSchedule(request, bindingResult)

        then:
        def exception = thrown(RequestException)
        exception.getMessage() == "'gatewayId' cannot be empty"
        exception.getHttpStatus() == HttpStatus.BAD_REQUEST
    }

    def "Should throw Request Exception with status 400 if there userId is not present"() {
        given: "Create Request"
        def request = CreatePutScheduleDTO.builder()
                .gatewayId("2134")
                .cronExpression("1234")
                .build()

        and: "Binding result has no errors"
        def bindingResult = Mock(BindingResult)
        bindingResult.hasErrors() >> false

        when:
        scheduleController.createSchedule(request, bindingResult)

        then:
        def exception = thrown(RequestException)
        exception.getMessage() == "'userId' cannot be empty"
        exception.getHttpStatus() == HttpStatus.BAD_REQUEST
    }

    def "Should throw Request Exception with status 400 if there are too many schedule specs when creating"() {
        given: "Create Request"
        def request = CreatePutScheduleDTO.builder()
                .gatewayId("123")
                .userId("2134")
                .cronExpression("1234")
                .interval(new CreatePutScheduleDTO.ScheduleIntervalSpecDTO("PT10S", "PT0S"))
                .build()

        and: "Binding result has no errors"
        def bindingResult = Mock(BindingResult)
        bindingResult.hasErrors() >> false

        when:
        scheduleController.createSchedule(request, bindingResult)

        then:
        def exception = thrown(RequestException)
        exception.getMessage() == "Please provide exactly one schedule configuration, either: 'calendar', 'interval' or 'cronExpression'"
        exception.getHttpStatus() == HttpStatus.BAD_REQUEST
    }

    def "Should throw Request Exception with status 400 if there are binding errors when updating"() {
        given: "Update Request"
        def request = CreatePutScheduleDTO.builder()
                .gatewayId("123")
                .userId("2134")
                .interval(new CreatePutScheduleDTO.ScheduleIntervalSpecDTO("PT10S", "PT0S"))
                .build()

        and: "Binding result has errors"
        def bindingResult = Mock(BindingResult)
        bindingResult.hasErrors() >> true
        FieldError fieldError = new FieldError("test", "test", "test")
        bindingResult.getFieldError() >> fieldError

        when:
        scheduleController.updateSchedule(request, bindingResult)

        then:
        def exception = thrown(RequestException)
        exception.getHttpStatus() == HttpStatus.BAD_REQUEST
    }


    def "Should throw exception when updating schedule with no schedule id"() {
        given: "update request"
        def update = CreatePutScheduleDTO.builder().build()

        and: "Binding result has no errors"
        def bindingResult = Mock(BindingResult)
        bindingResult.hasErrors() >> false

        when:
        scheduleController.updateSchedule(update, bindingResult)

        then:
        def exception = thrown(RequestException)
        exception.getMessage() == "Please provide a 'scheduleId' in the request body to update a schedule"
        exception.getHttpStatus() == HttpStatus.BAD_REQUEST
    }

    def "Should throw Request Exception with status 400 if there are too many schedule specs when updating"() {
        given: "Update Request"
        def request = CreatePutScheduleDTO.builder()
                .scheduleId("1234")
                .gatewayId("123")
                .userId("2134")
                .cronExpression("1234")
                .interval(new CreatePutScheduleDTO.ScheduleIntervalSpecDTO("PT10S", "PT0S"))
                .build()

        and: "Binding result has no errors"
        def bindingResult = Mock(BindingResult)
        bindingResult.hasErrors() >> false

        when:
        scheduleController.updateSchedule(request, bindingResult)

        then:
        def exception = thrown(RequestException)
        exception.getMessage() == "Please only provide zero or one schedule configurations, either: 'calendar', 'interval' or 'cronExpression'"
        exception.getHttpStatus() == HttpStatus.BAD_REQUEST
    }

    def "Should return 200 and return updated schedule"() {
        given: "Update Request"
        def request = CreatePutScheduleDTO.builder()
                .scheduleId("1234")
                .gatewayId("123")
                .userId("2134")
                .interval(new CreatePutScheduleDTO.ScheduleIntervalSpecDTO("PT10S", "PT0S"))
                .build()

        and: "Binding result has no error"
        def bindingResult = Mock(BindingResult)
        bindingResult.hasErrors() >> false

        and: "Service returns created schedule"
        service.updateSchedule(request) >> new ScheduleDescriptionDTO()

        when:
        def response = scheduleController.updateSchedule(request, bindingResult)

        then:
        response.getStatusCode() == HttpStatus.OK
    }

    def "Should return 200 and return modified schedule number"() {
        given: "Update Request"
        def userId = Optional.of("1234")
        def gatewayId = Optional.of("1234")
        def request = SchedulePatchDTO.builder()
                .gatewayId("123")
                .paused(true)
                .build()

        and: "Binding result has no error"
        def bindingResult = Mock(BindingResult)
        bindingResult.hasErrors() >> false

        and: "Service returns created schedule"
        service.batchUpdateSchedules(userId, gatewayId, request) >> ModifiedDTO.builder().message("test").totalModified(1).build()

        when:
        def response = scheduleController.batchUpdateSchedules(userId, gatewayId, request, bindingResult)

        then:
        response.getStatusCode() == HttpStatus.OK
    }

    def "Should throw Request Exception with status 400 if there are binding errors when batch updating"() {
        given: "Update Request"
        def userId = Optional.of("1234")
        def gatewayId = Optional.of("1234")
        def request = SchedulePatchDTO.builder()
                .gatewayId("123")
                .paused(true)
                .build()

        and: "Binding result has errors"
        def bindingResult = Mock(BindingResult)
        bindingResult.hasErrors() >> true
        FieldError fieldError = new FieldError("test", "test", "test")
        bindingResult.getFieldError() >> fieldError

        when:
        scheduleController.batchUpdateSchedules(userId, gatewayId, request, bindingResult)

        then:
        def exception = thrown(RequestException)
        exception.getHttpStatus() == HttpStatus.BAD_REQUEST
    }

    def "Should return 200 when deleting schedules by filter"() {
        given: "Update Request"
        def userId = Optional.of("1234")
        def gatewayId = Optional.of("1234")


        and: "Service returns created schedule"
        service.deleteSchedulesByFilter(userId, gatewayId) >> ModifiedDTO.builder().message("test").totalModified(1).build()

        when:
        def response = scheduleController.batchDeleteSchedules(userId, gatewayId)

        then:
        response.getStatusCode() == HttpStatus.OK
    }

    def "Should return 200 when returning the schedule count"() {
        given: "Update Request"
        def userId = Optional.of("1234")
        def gatewayId = Optional.of("1234")


        and: "Service returns created schedule"
        service.getScheduleCount(userId, gatewayId) >> CountDTO.builder().total(1).build()

        when:
        def response = scheduleController.getScheduleNumber(userId, gatewayId)

        then:
        response.getStatusCode() == HttpStatus.OK
        response.getBody().getTotal() == 1
    }

    def "Should return 203 when deleting schedule by id"() {
        given:
        service.deleteScheduleById("123") >> { return null }

        when:
        def response = scheduleController.deleteById("123")

        then:
        response.getStatusCode() == HttpStatus.NO_CONTENT
    }

    def "Should return 200 when getting Schedule by id"() {
        given:
        def id = "123"
        def dto = new ScheduleDescriptionDTO()
        dto.setScheduleId("123")
        dto.setSchedule(
                ScheduleDescriptionDTO.ScheduleDTO.builder()
                        .state(ScheduleState.newBuilder()
                                .setNote("test")
                                .setPaused(false)
                                .build())
                        .build()
        )
        dto.setInfo(new ScheduleInfo(0, 0, 0, [], [], [], Instant.now(), null))

        and:
        service.getScheduleById(id) >> dto

        when:
        def response = scheduleController.getScheduleById("123")

        then:
        response.getStatusCode() == HttpStatus.OK
        response.getBody().getInfo() == dto.getInfo()
        response.getBody().getSchedule() == dto.getSchedule()
        !response.getBody().getSchedule().getState().paused
    }
}
