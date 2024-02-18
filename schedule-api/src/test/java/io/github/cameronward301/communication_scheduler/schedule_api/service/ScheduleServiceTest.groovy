package io.github.cameronward301.communication_scheduler.schedule_api.service


import io.github.cameronward301.communication_scheduler.schedule_api.exception.RequestException
import io.github.cameronward301.communication_scheduler.schedule_api.helper.DtoConverter
import io.github.cameronward301.communication_scheduler.schedule_api.helper.ScheduleHelper
import io.github.cameronward301.communication_scheduler.schedule_api.model.CreateScheduleDTO
import io.github.cameronward301.communication_scheduler.schedule_api.model.SchedulePatchDTO
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.temporal.api.common.v1.Payload
import io.temporal.api.enums.v1.ScheduleOverlapPolicy
import io.temporal.client.schedules.*
import io.temporal.common.SearchAttributeKey
import io.temporal.common.SearchAttributes
import io.temporal.common.converter.DataConverter
import io.temporal.common.converter.DefaultDataConverter
import io.temporal.workflow.Functions
import lombok.extern.slf4j.Slf4j
import org.jetbrains.annotations.NotNull
import org.modelmapper.ModelMapper
import org.springframework.http.HttpStatus
import spock.lang.Specification

import javax.annotation.Nonnull
import java.time.Instant
import java.util.stream.Stream

import static java.lang.String.format

@Slf4j
class ScheduleServiceTest extends Specification {


    def scheduleClient = Mock(ScheduleClient)
    def modelMapper = new ModelMapper()
    def scheduleHelper = new ScheduleHelper("test-queue", new DtoConverter())
    private ScheduleService scheduleService


    def setup() {
        scheduleService = new ScheduleService(scheduleClient, modelMapper, scheduleHelper)
    }

    def "Should return all schedules"() {
        given:
        def pageNumber = "0"
        def pageSize = "10"
        def userId = Optional.of("123")
        def gatewayId = Optional.of("abc")
        def scheduleId = "1234"

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
                Map.of("userId", ["123"], "gatewayId", ["abc"])
        )

        and:
        scheduleClient.listSchedules() >> Stream.of(schedule)

        when:
        def response = scheduleService.getAllSchedules(pageNumber, pageSize, userId, gatewayId)

        then:
        response.getContent().get(0) == schedule
        response.getTotalElements() == 1
        response.getPageable().getPageNumber() == 0
        response.getPageable().getPageSize() == Integer.parseInt(pageSize)
    }

    def "Calling get all Schedules with invalid pageNumber throws exception"() {
        given:
        def pageNumber = "a"
        def pageSize = "10"
        def userId = Optional.of("123")
        def gatewayId = Optional.of("abc")

        when:
        scheduleService.getAllSchedules(pageNumber, pageSize, userId, gatewayId)

        then:
        def exception = thrown(RequestException)
        exception.getHttpStatus() == HttpStatus.BAD_REQUEST
        exception.getMessage() == "Invalid values for pageNumber of pageSize"
    }

    def "Should createSchedule"() {
        given:
        def createDTO = CreateScheduleDTO.builder()
                .gatewayId("123")
                .userId("1234")
                .paused(true)
                .cronExpression("1 * * * MON")
                .build()
        def id = UUID.randomUUID().toString()
        def handle = Mock(ScheduleHandle)

        and:
        scheduleClient.createSchedule(_ as String, _ as Schedule, _ as ScheduleOptions) >> handle

        and:
        handle.describe() >> createScheduleDescription(id, [], [], [createDTO.getCronExpression()], SearchAttributes.newBuilder()
                .set(SearchAttributeKey.forText("userId"), createDTO.getUserId())
                .set(SearchAttributeKey.forText("gatewayId"), createDTO.getGatewayId())
                .set(SearchAttributeKey.forText("scheduleId"), id)
                .build()
        )

        when:
        def response = scheduleService.createSchedule(createDTO)

        then:
        response.getId() == id
        response.getSchedule().getSpec().getCronExpressions().get(0) == createDTO.getCronExpression()
        response.getSearchAttributes().get("userId").get(0) == createDTO.getUserId()
        response.getSearchAttributes().get("gatewayId").get(0) == createDTO.getGatewayId()
        response.getSearchAttributes().get("scheduleId").get(0) == id
    }

    def "should update schedule"() {
        given: "Schedule DTO"
        def id = UUID.randomUUID().toString()
        def scheduleDto = CreateScheduleDTO.builder()
                .scheduleId(id)
                .gatewayId("123")
                .userId("1234")
                .paused(true)
                .cronExpression("1 * * * MON")
                .build()

        and: "Schedule handle"
        def scheduleHandle = getScheduleHandle(scheduleDto)
        scheduleClient.getHandle(id) >> scheduleHandle
        scheduleClient.createSchedule(_ as String, _ as Schedule, _ as ScheduleOptions) >> scheduleHandle


        when: "Calling update"
        def response = scheduleService.updateSchedule(scheduleDto)


        then:
        response.getId() == id
        response.getSchedule().getSpec().getCronExpressions().get(0) == scheduleDto.getCronExpression()
        response.getSearchAttributes().get("userId").get(0) == scheduleDto.getUserId()
        response.getSearchAttributes().get("gatewayId").get(0) == scheduleDto.getGatewayId()
        response.getSearchAttributes().get("scheduleId").get(0) == id
    }

    private ScheduleDescription createScheduleDescription(
            String id,
            List<ScheduleCalendarSpec> calendars,
            List<ScheduleIntervalSpec> intervals,
            List<String> cronExpressions,
            SearchAttributes searchAttributes

    ) {
        return new ScheduleDescription(
                id,
                new ScheduleInfo(
                        0,
                        0,
                        0,
                        [],
                        [],
                        [],
                        Instant.now(),
                        Instant.now()
                ),
                Schedule.newBuilder()
                        .setAction(Mock(ScheduleAction))
                        .setSpec(ScheduleSpec.newBuilder()
                                .setCalendars(calendars)
                                .setCronExpressions(cronExpressions)
                                .setIntervals(intervals)
                                .setStartAt(Instant.now())
                                .setTimeZoneName("UTC")
                                .build()
                        )
                        .build(),
                Map.of(
                        "userId", [searchAttributes.getUntypedValues().get(SearchAttributeKey.forText("userId"))],
                        "gatewayId", [searchAttributes.getUntypedValues().get(SearchAttributeKey.forText("gatewayId"))],
                        "scheduleId", [searchAttributes.getUntypedValues().get(SearchAttributeKey.forText("scheduleId"))]
                ),
                searchAttributes,
                Map.of(),
                Mock(DataConverter))
    }

    def "Should throw exception when updating schedule with no schedule id"() {
        given: "update request"
        def update = CreateScheduleDTO.builder().build()

        when:
        scheduleService.updateSchedule(update)

        then:
        def exception = thrown(RequestException)
        exception.getMessage() == "Please provide a 'scheduleId' in the request body to update a schedule"
        exception.getHttpStatus() == HttpStatus.BAD_REQUEST
    }

    def "Should throw exception if id is not valid when updating a schedule"() {
        given: "update request"
        def update = CreateScheduleDTO.builder()
                .scheduleId("123")
                .build()

        and: "getHandle throws not found exception"
        scheduleClient.getHandle(update.getScheduleId()) >> { throw new ScheduleException(new StatusRuntimeException(Status.NOT_FOUND)) }

        when:
        scheduleService.updateSchedule(update)

        then:
        def exception = thrown(RequestException)
        exception.getMessage() == format("Could not find Schedule with Id: %s", update.getScheduleId())
        exception.getHttpStatus() == HttpStatus.NOT_FOUND
    }

    def "Should throw runtime exception when updating schedule encounters an error"() {
        given: "update request"
        def update = CreateScheduleDTO.builder()
                .scheduleId("123")
                .build()

        and: "getHandle throws not found exception"
        scheduleClient.getHandle(update.getScheduleId()) >> { throw new ScheduleException(new RuntimeException("test-exception")) }

        when:
        scheduleService.updateSchedule(update)

        then:
        def exception = thrown(RuntimeException)
        exception.getCause().getCause().getMessage() == "test-exception"
    }

    def "Should batch update schedules"() {
        given:
        def userId = Optional.of("123")
        def gatewayId = Optional.of("abc")
        def patchDTO = SchedulePatchDTO.builder()
                .paused(true)
                .gatewayId("efg")
                .build()
        def scheduleId = UUID.randomUUID().toString()

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
                Map.of("userId", ["123"], "gatewayId", ["abc"])
        )

        and:
        scheduleClient.listSchedules() >> Stream.of(schedule)
        scheduleClient.getHandle(scheduleId) >> Mock(ScheduleHandle)

        when:
        def response = scheduleService.batchUpdateSchedules(userId, gatewayId, patchDTO)

        then:
        response.getMessage() == "Completed"
        response.getTotalModified() == 1
        1 * scheduleClient.getHandle(scheduleId).delete()
        1 * scheduleClient.createSchedule(scheduleId, _ as Schedule, _ as ScheduleOptions)
    }

    def "Should throw exception if userId and gatewayId is empty when trying to batch update schedules"() {
        given:
        def patchDto = SchedulePatchDTO.builder().build()

        when:
        scheduleService.batchUpdateSchedules(Optional.empty(), Optional.empty(), patchDto)

        then:
        def response = thrown(RequestException)
        response.getHttpStatus() == HttpStatus.BAD_REQUEST
        response.getMessage() == "Must supply at least one of 'userId' or 'gatewayId' as a query parameter"
    }

    def "Should get schedule by id"() {
        given:
        def id = "123"
        def userId = "1234"
        def gatewayId = "abc"


        and:
        scheduleClient.getHandle(id) >> getScheduleHandle(CreateScheduleDTO.builder()
                .scheduleId(id)
                .gatewayId(gatewayId)
                .userId(userId)
                .build()
        )

        when:
        def response = scheduleService.getScheduleById(id)

        then:
        response.getSearchAttributes().get("gatewayId").get(0) == gatewayId
        response.getSearchAttributes().get("userId").get(0) == userId
        response.getSearchAttributes().get("scheduleId").get(0) == id
    }

    def "Should throw exception if schedule not found when get by id"() {
        given: "id"
        def id = "123"

        and: "getHandle throws not found exception"
        scheduleClient.getHandle(id) >> { throw new ScheduleException(new StatusRuntimeException(Status.NOT_FOUND)) }

        when:
        scheduleService.getScheduleById(id)

        then:
        def exception = thrown(RequestException)
        exception.getMessage() == format("Could not find Schedule with Id: %s", id)
        exception.getHttpStatus() == HttpStatus.NOT_FOUND
    }

    def "Should delete schedules by id"() {
        given:
        def id = "123"

        and:
        scheduleClient.getHandle(id) >> getScheduleHandle(CreateScheduleDTO.builder().scheduleId(id).build())

        when:
        scheduleService.deleteScheduleById(id)

        then:
        notThrown(Exception)

    }

    def "Should throw exception if schedule not found when delete by id"() {
        given: "id"
        def id = "123"

        and: "getHandle throws not found exception"
        scheduleClient.getHandle(id) >> { throw new ScheduleException(new StatusRuntimeException(Status.NOT_FOUND)) }

        when:
        scheduleService.deleteScheduleById(id)

        then:
        def exception = thrown(RequestException)
        exception.getMessage() == format("Could not find Schedule with Id: %s", id)
        exception.getHttpStatus() == HttpStatus.NOT_FOUND
    }

    def "Should get schedule count"() {
        given:
        def userId = Optional.of("123")
        def gatewayId = Optional.of("abc")

        def scheduleId = UUID.randomUUID().toString()

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
                Map.of("userId", ["123"], "gatewayId", ["abc"])
        )

        and:
        scheduleClient.listSchedules() >> Stream.of(schedule)

        when:
        def response = scheduleService.getScheduleCount(userId, gatewayId)

        then:
        response.getTotal() == 1

    }

    def "Should delete schedules by filter"() {
        given:
        def userId = Optional.of("123")
        def gatewayId = Optional.of("abc")

        def scheduleId = UUID.randomUUID().toString()

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
                Map.of("userId", ["123"], "gatewayId", ["abc"])
        )

        and:
        scheduleClient.listSchedules() >> Stream.of(schedule)
        scheduleClient.getHandle(scheduleId) >> getScheduleHandle(CreateScheduleDTO.builder().scheduleId(scheduleId).build())

        when:
        def response = scheduleService.deleteSchedulesByFilter(userId, gatewayId)

        then:
        response.getMessage() == "Successfully Deleted"
        response.getTotalModified() == 1
    }

    def "Should throw exception if userId and gatewayId are empty when deleting by filter"() {
        when:
        scheduleService.deleteSchedulesByFilter(Optional.empty(), Optional.empty())

        then:
        def exception = thrown(RequestException)
        exception.getMessage() == "Must provide at least one of 'gatewayId' or 'userId' filters"
        exception.getHttpStatus() == HttpStatus.BAD_REQUEST
    }

    private ScheduleHandle getScheduleHandle(CreateScheduleDTO createDTO) {
        return new ScheduleHandle() {
            @Override
            String getId() {
                return createDTO.getScheduleId()
            }

            @Override
            void backfill(List<ScheduleBackfill> backfills) {

            }

            @Override
            void delete() {

            }

            @Override
            ScheduleDescription describe() {
                return createScheduleDescription(createDTO.getScheduleId(), [], [], [createDTO.getCronExpression()], SearchAttributes.newBuilder()
                        .set(SearchAttributeKey.forText("userId"), createDTO.getUserId())
                        .set(SearchAttributeKey.forText("gatewayId"), createDTO.getGatewayId())
                        .set(SearchAttributeKey.forText("scheduleId"), createDTO.getScheduleId())
                        .build())
            }

            @Override
            void pause(@NotNull @Nonnull String note) {

            }

            @Override
            void pause() {

            }

            @Override
            void trigger(ScheduleOverlapPolicy overlapPolicy) {

            }

            @Override
            void trigger() {

            }

            @Override
            void unpause(@NotNull @Nonnull String note) {

            }

            @Override
            void unpause() {

            }

            @Override
            void update(Functions.Func1<ScheduleUpdateInput, ScheduleUpdate> updater) {
                ScheduleUpdateInput input = new ScheduleUpdateInput(describe())
                updater.apply(input)

            }
        }
    }


}
