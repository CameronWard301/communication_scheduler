package io.github.cameronward301.communication_scheduler.schedule_api.helper;

import io.github.cameronward301.communication_scheduler.schedule_api.exception.RequestException;
import io.github.cameronward301.communication_scheduler.schedule_api.model.CreatePutScheduleDTO;
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.CommunicationWorkflow;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.schedules.ScheduleActionStartWorkflow;
import io.temporal.client.schedules.ScheduleListDescription;
import io.temporal.client.schedules.ScheduleSpec;
import io.temporal.client.schedules.ScheduleState;
import io.temporal.common.SearchAttributeKey;
import io.temporal.common.SearchAttributes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Predicate;

import static java.lang.String.format;

/**
 * Builds a number of Temporal Scheduling Objects from DTOs or other helpful converter functions
 */
@Component
public class ScheduleHelper {
    private final String taskQueue;

    private final DtoConverter dtoConverter;

    public ScheduleHelper(@Value("${temporal-properties.taskQueue}") String taskQueue,
                          DtoConverter dtoConverter) {
        this.taskQueue = taskQueue;
        this.dtoConverter = dtoConverter;
    }

    /**
     * Creates a new schedule action for a new schedule to be created
     *
     * @param createPutScheduleDTO the schedule to create
     * @return ScheduleAction containing the workflow details and search attributes
     */
    public ScheduleActionStartWorkflow getScheduleAction(CreatePutScheduleDTO createPutScheduleDTO) {
        return ScheduleActionStartWorkflow.newBuilder()
                .setWorkflowType(CommunicationWorkflow.class)
                .setArguments(Map.of(
                        "userId", createPutScheduleDTO.getUserId(),
                        "gatewayId", createPutScheduleDTO.getGatewayId()
                ))
                .setOptions(WorkflowOptions.newBuilder()
                        .setTaskQueue(taskQueue)
                        .setTypedSearchAttributes(getSearchAttributes(createPutScheduleDTO))
                        .setWorkflowId(
                                format("%s:%s:%s:",
                                        createPutScheduleDTO.getGatewayId(),
                                        createPutScheduleDTO.getUserId(),
                                        createPutScheduleDTO.getScheduleId()))
                        .build())
                .build();
    }

    /**
     * Create a Schedule state from a paused parameter
     *
     * @param paused schedule paused state
     * @return schedule state object
     */
    public ScheduleState getScheduleState(boolean paused) {
        return ScheduleState.newBuilder()
                .setNote("")
                .setPaused(paused)
                .build();
    }

    /**
     * Create a schedule spec from a DTO. Must provide exactly one schedule configuration
     *
     * @param createPutScheduleDTO to create the schedule spec from
     * @return a new ScheduleSpec object
     */
    public ScheduleSpec getScheduleSpec(CreatePutScheduleDTO createPutScheduleDTO) {
        if (createPutScheduleDTO.getCalendar() != null) {
            if (createPutScheduleDTO.getCalendar().getDayOfMonth() == null
                    || createPutScheduleDTO.getCalendar().getDayOfWeek() == null
                    || createPutScheduleDTO.getCalendar().getMonth() == null
                    || createPutScheduleDTO.getCalendar().getYear() == null
                    || createPutScheduleDTO.getCalendar().getHour() == null
                    || createPutScheduleDTO.getCalendar().getMinutes() == null
                    || createPutScheduleDTO.getCalendar().getSeconds() == null) {
                throw new RequestException("Invalid calendar format, all fields must be present or empty array: dayOfMonth, dayOfWeek, month, year, hour, minutes and seconds", HttpStatus.BAD_REQUEST);
            }
            return ScheduleSpec.newBuilder()
                    .setCalendars(List.of(dtoConverter.getCalendar(createPutScheduleDTO.getCalendar())))
                    .build();
        }
        if (createPutScheduleDTO.getInterval() != null) {
            return ScheduleSpec.newBuilder()
                    .setIntervals(List.of(dtoConverter.getInterval(createPutScheduleDTO.getInterval())))
                    .build();
        }
        if (createPutScheduleDTO.getCronExpression() != null && !createPutScheduleDTO.getCronExpression().isEmpty()) {
            return ScheduleSpec.newBuilder()
                    .setCronExpressions(List.of(createPutScheduleDTO.getCronExpression()))
                    .build();
        }
        throw new RequestException("Please provide exactly one schedule configuration, either: 'calendar', 'interval' or 'cronExpression'", HttpStatus.BAD_REQUEST);
    }

    /**
     * Generate search attributes from the create request
     *
     * @param createPutScheduleDTO of the schedule to be created
     * @return SearchAttributes object from the userId, gatewayId and scheduleId
     */
    public SearchAttributes getSearchAttributes(CreatePutScheduleDTO createPutScheduleDTO) {
        return SearchAttributes.newBuilder()
                .set(SearchAttributeKey.forKeyword("userId"), createPutScheduleDTO.getUserId())
                .set(SearchAttributeKey.forKeyword("gatewayId"), createPutScheduleDTO.getGatewayId())
                .set(SearchAttributeKey.forKeyword("scheduleId"), createPutScheduleDTO.getScheduleId())
                .build();
    }

    /**
     * Create a predicate filter to sort through schedules
     *
     * @param userId    to add to the predicate
     * @param gatewayId to add to the predicate
     * @return a predicate containing the userId or gatewayId filters or both
     */
    public Predicate<ScheduleListDescription> getStreamFilter(Optional<String> userId, Optional<String> gatewayId) {
        Predicate<ScheduleListDescription> predicate = scheduleListDescription -> true;

        if (userId.isPresent()) {
            predicate = predicate.and(scheduleListDescription -> Objects.equals(scheduleListDescription.getSearchAttributes().get("userId"), Collections.singletonList(userId.get())));
        }

        if (gatewayId.isPresent()) {
            predicate = predicate.and(scheduleListDescription -> Objects.equals(scheduleListDescription.getSearchAttributes().get("gatewayId"), Collections.singletonList(gatewayId.get())));
        }

        return predicate;
    }
}
