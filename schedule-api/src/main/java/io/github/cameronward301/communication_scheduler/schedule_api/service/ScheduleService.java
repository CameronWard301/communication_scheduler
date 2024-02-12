package io.github.cameronward301.communication_scheduler.schedule_api.service;

import io.github.cameronward301.communication_scheduler.schedule_api.exception.RequestException;
import io.github.cameronward301.communication_scheduler.schedule_api.herlper.DtoConverter;
import io.github.cameronward301.communication_scheduler.schedule_api.model.CreateScheduleDTO;
import io.github.cameronward301.communication_scheduler.schedule_api.model.ScheduleDescriptionDTO;
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.CommunicationWorkflow;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.schedules.*;
import io.temporal.common.SearchAttributeKey;
import io.temporal.common.SearchAttributes;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
@Slf4j
public class ScheduleService {

    private final ScheduleClient scheduleClient;

    private final String taskQueue;

    private final DtoConverter dtoConverter;
    private final ModelMapper modelMapper;

    public ScheduleService(
            ScheduleClient scheduleClient,
            @Value("${temporal-properties.taskQueue}") String taskQueue,
            DtoConverter dtoConverter,
            ModelMapper modelMapper
    ) {
        this.scheduleClient = scheduleClient;
        this.taskQueue = taskQueue;
        this.dtoConverter = dtoConverter;
        this.modelMapper = modelMapper;
    }

    public Page<ScheduleListDescription> getAllSchedules(
            String pageNumber,
            String pageSize,
            Optional<String> userId,
            Optional<String> gatewayId
    ) {
        try {
            int pageNumberInt = Integer.parseInt(pageNumber);
            int pageSizeInt = Integer.parseInt(pageSize);
            List<ScheduleListDescription> filteredSchedules = scheduleClient.listSchedules().filter(getStreamFilter(userId, gatewayId)).toList();
            int totalResults = filteredSchedules.size();

            return new PageImpl<>(filteredSchedules.stream().skip((long) pageNumberInt * pageSizeInt).limit(pageSizeInt).collect(Collectors.toList()), PageRequest.of(pageNumberInt, pageSizeInt), totalResults);
        } catch (NumberFormatException e) {
            throw new RequestException("Invalid values for pageNumber of pageSize", HttpStatus.BAD_REQUEST);
        }
    }

    // At least one of schedules will not be null, todo update this
    // Creates workflow with ID format GATEWAY_ID:USER_ID:SCHEDULE_ID:<scheduleTime>
    public ScheduleDescriptionDTO createSchedule(CreateScheduleDTO createScheduleDTO) {
        String scheduleId = UUID.randomUUID().toString();
        return modelMapper.map(scheduleClient.createSchedule(scheduleId,
                Schedule.newBuilder()
                        .setState(
                                ScheduleState.newBuilder()
                                        .setPaused(createScheduleDTO.isPaused())
                                        .setNote("")
                                        .build()
                        )
                        .setSpec(getScheduleSpec(createScheduleDTO))
                        .setAction(
                                ScheduleActionStartWorkflow.newBuilder()
                                        .setWorkflowType(CommunicationWorkflow.class)
                                        .setArguments(Map.of(
                                                "userId", createScheduleDTO.getUserId(),
                                                "gatewayId", createScheduleDTO.getGatewayId()
                                        ))
                                        .setOptions(WorkflowOptions.newBuilder()
                                                .setTaskQueue(taskQueue)
                                                .setTypedSearchAttributes(getSearchAttributes(createScheduleDTO, scheduleId))
                                                .setWorkflowId(
                                                        format("%s:%s:%s:",
                                                                createScheduleDTO.getGatewayId(),
                                                                createScheduleDTO.getUserId(),
                                                                scheduleId))
                                                .build())
                                        .build()
                        )
                        .build(),
                ScheduleOptions.newBuilder()
                        .setTypedSearchAttributes(getSearchAttributes(createScheduleDTO, scheduleId))
                        .build()
                ).describe(), ScheduleDescriptionDTO.class);
    }

    public ScheduleDescriptionDTO getScheduleById(String scheduleId) {
        try {
            return modelMapper.map(scheduleClient.getHandle(scheduleId).describe(), ScheduleDescriptionDTO.class);
        } catch (ScheduleException e) {
            log.debug(e.getMessage());
            if (Objects.equals(((StatusRuntimeException) e.getCause()).getStatus().getCode(), Status.NOT_FOUND.getCode())) {
                throw new RequestException(format("Could not find Schedule with Id: %s", scheduleId), HttpStatus.NOT_FOUND);
            }
            throw new RuntimeException(e);
        }
    }

    public void deleteScheduleById(String scheduleId) {
        try {
            scheduleClient.getHandle(scheduleId).delete();
        } catch (ScheduleException e) {
            log.debug(e.getMessage());
            if (Objects.equals(((StatusRuntimeException) e.getCause()).getStatus().getCode(), Status.NOT_FOUND.getCode())) {
                throw new RequestException(format("Could not find Schedule with Id: %s", scheduleId), HttpStatus.NOT_FOUND);
            }
            throw new RuntimeException(e);
        }
    }

    public void deleteSchedulesByFilter(Optional<String> userId, Optional<String> gatewayId) {
        if (userId.isEmpty() && gatewayId.isEmpty()) {
            throw new RequestException("Must provide at least one of 'gatewayId' or 'userId' filters", HttpStatus.BAD_REQUEST);
        }
        List<ScheduleListDescription> filteredSchedules = scheduleClient.listSchedules().filter(getStreamFilter(userId, gatewayId)).toList();
        for (ScheduleListDescription schedule: filteredSchedules) {
            deleteScheduleById(schedule.getScheduleId());
        }

    }

    private ScheduleSpec getScheduleSpec(CreateScheduleDTO createScheduleDTO) {
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
        if (!createScheduleDTO.getCronExpression().isEmpty()) {
            return ScheduleSpec.newBuilder()
                    .setCronExpressions(List.of(createScheduleDTO.getCronExpression()))
                    .build();
        }
        throw new RequestException("Please provide exactly one schedule configuration, either: 'calendar', 'interval' or 'cronExpression'", HttpStatus.BAD_REQUEST);
    }

    private SearchAttributes getSearchAttributes(CreateScheduleDTO createScheduleDTO, String scheduleId) {
        return SearchAttributes.newBuilder()
                .set(SearchAttributeKey.forKeyword("userId"), createScheduleDTO.getUserId())
                .set(SearchAttributeKey.forKeyword("gatewayId"), createScheduleDTO.getGatewayId())
                .set(SearchAttributeKey.forKeyword("scheduleId"), scheduleId)
                .build();
    }

    private Predicate<ScheduleListDescription> getStreamFilter (Optional<String> userId, Optional<String> gatewayId){
        Predicate<ScheduleListDescription> predicate = scheduleListDescription -> true;

        if (userId.isPresent()) {
            predicate = predicate.and(scheduleListDescription -> Objects.equals(scheduleListDescription.getSearchAttributes().get("userId"), Collections.singletonList(userId.get())));
        }

        if (gatewayId.isPresent()){
            predicate = predicate.and(scheduleListDescription -> Objects.equals(scheduleListDescription.getSearchAttributes().get("gatewayId"), Collections.singletonList(gatewayId.get())));
        }

        return predicate;
    }
}
