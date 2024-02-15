package io.github.cameronward301.communication_scheduler.schedule_api.helper;

import io.github.cameronward301.communication_scheduler.schedule_api.exception.RequestException;
import io.github.cameronward301.communication_scheduler.schedule_api.model.CreateScheduleDTO;
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

@Component
public class ScheduleHelper {
    private final String taskQueue;

    private final DtoConverter dtoConverter;

    public ScheduleHelper(@Value("${temporal-properties.taskQueue}") String taskQueue,
                          DtoConverter dtoConverter) {
        this.taskQueue = taskQueue;
        this.dtoConverter = dtoConverter;
    }

    public ScheduleActionStartWorkflow getScheduleAction(CreateScheduleDTO createScheduleDTO) {
        return ScheduleActionStartWorkflow.newBuilder()
                .setWorkflowType(CommunicationWorkflow.class)
                .setArguments(Map.of(
                        "userId", createScheduleDTO.getUserId(),
                        "gatewayId", createScheduleDTO.getGatewayId()
                ))
                .setOptions(WorkflowOptions.newBuilder()
                        .setTaskQueue(taskQueue)
                        .setTypedSearchAttributes(getSearchAttributes(createScheduleDTO))
                        .setWorkflowId(
                                format("%s:%s:%s:",
                                        createScheduleDTO.getGatewayId(),
                                        createScheduleDTO.getUserId(),
                                        createScheduleDTO.getScheduleId()))
                        .build())
                .build();
    }

    public ScheduleState getScheduleState(boolean paused) {
        return ScheduleState.newBuilder()
                .setNote("")
                .setPaused(paused)
                .build();
    }

    public ScheduleSpec getScheduleSpec(CreateScheduleDTO createScheduleDTO) {
        if (createScheduleDTO.getCalendar() != null) {
            return ScheduleSpec.newBuilder()
                    .setCalendars(List.of(dtoConverter.getCalendar(createScheduleDTO.getCalendar())))
                    .build();
        }
        if (createScheduleDTO.getInterval() != null) {
            return ScheduleSpec.newBuilder()
                    .setIntervals(List.of(dtoConverter.getInterval(createScheduleDTO.getInterval())))
                    .build();
        }
        if (createScheduleDTO.getCronExpression() != null && !createScheduleDTO.getCronExpression().isEmpty()) {
            return ScheduleSpec.newBuilder()
                    .setCronExpressions(List.of(createScheduleDTO.getCronExpression()))
                    .build();
        }
        throw new RequestException("Please provide exactly one schedule configuration, either: 'calendar', 'interval' or 'cronExpression'", HttpStatus.BAD_REQUEST);
    }

    public SearchAttributes getSearchAttributes(CreateScheduleDTO createScheduleDTO) {
        return SearchAttributes.newBuilder()
                .set(SearchAttributeKey.forKeyword("userId"), createScheduleDTO.getUserId())
                .set(SearchAttributeKey.forKeyword("gatewayId"), createScheduleDTO.getGatewayId())
                .set(SearchAttributeKey.forKeyword("scheduleId"), createScheduleDTO.getScheduleId())
                .build();
    }

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
