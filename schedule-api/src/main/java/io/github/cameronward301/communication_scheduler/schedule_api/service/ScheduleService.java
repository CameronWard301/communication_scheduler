package io.github.cameronward301.communication_scheduler.schedule_api.service;
// Code adapted from: https://github.com/temporalio/samples-java/blob/main/core/src/main/java/io/temporal/samples/hello/HelloSchedules.java

import io.github.cameronward301.communication_scheduler.schedule_api.exception.RequestException;
import io.github.cameronward301.communication_scheduler.schedule_api.helper.ScheduleHelper;
import io.github.cameronward301.communication_scheduler.schedule_api.model.*;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.temporal.client.schedules.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleClient scheduleClient;
    private final ModelMapper modelMapper;
    private final ScheduleHelper scheduleHelper;

    /**
     * Get all schedules matching the given filter
     *
     * @param pageNumber of paged results
     * @param pageSize   how many results to include in a page
     * @param userId     to filter results
     * @param gatewayId  to filter results
     * @return a page matching the filters with the correct paging parameters
     */

    public Page<ScheduleListDescription> getAllSchedules(
            String pageNumber,
            String pageSize,
            Optional<String> userId,
            Optional<String> gatewayId
    ) {
        try {
            int pageNumberInt = Integer.parseInt(pageNumber);
            int pageSizeInt = Integer.parseInt(pageSize);
            List<ScheduleListDescription> filteredSchedules = scheduleClient.listSchedules().filter(scheduleHelper.getStreamFilter(userId, gatewayId)).toList();
            int totalResults = filteredSchedules.size();

            return new PageImpl<>(filteredSchedules.stream().skip((long) pageNumberInt * pageSizeInt).limit(pageSizeInt).collect(Collectors.toList()), PageRequest.of(pageNumberInt, pageSizeInt), totalResults);
        } catch (NumberFormatException e) {
            throw new RequestException("Invalid values for pageNumber of pageSize", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Creates a new schedule from the request DTO
     * Schedule will create workflows with the id: GATEWAY_ID:USER_ID:SCHEDULE_ID:<scheduleTime>
     * Note that the scheduleSpec must not be null and contain at least one specification
     *
     * @param createScheduleDTO to create the schedule from
     * @return the created schedule DTO
     */
    public ScheduleDescriptionDTO createSchedule(CreateScheduleDTO createScheduleDTO) {
        createScheduleDTO.setScheduleId(UUID.randomUUID().toString());
        return modelMapper.map(scheduleClient.createSchedule(createScheduleDTO.getScheduleId(),
                Schedule.newBuilder()
                        .setState(scheduleHelper.getScheduleState(createScheduleDTO.isPaused()))
                        .setSpec(scheduleHelper.getScheduleSpec(createScheduleDTO))
                        .setAction(scheduleHelper.getScheduleAction(createScheduleDTO))
                        .build(),
                ScheduleOptions.newBuilder()
                        .setTypedSearchAttributes(scheduleHelper.getSearchAttributes(createScheduleDTO))
                        .build()
        ).describe(), ScheduleDescriptionDTO.class);
    }

    /**
     * Update a given schedule from a dto. Removes the old schedule and creates a new schedule to update the search attributes
     * It may be possible to update schedule search attributes in a future temporal SDK version
     *
     * @param scheduleDTO to update (contains the existing schedule ID)
     * @return the updated schedule result
     */
    public ScheduleDescriptionDTO updateSchedule(CreateScheduleDTO scheduleDTO) {
        if (scheduleDTO.getScheduleId() == null || scheduleDTO.getScheduleId().isBlank()) {
            throw new RequestException("Please provide a 'scheduleId' in the request body to update a schedule", HttpStatus.BAD_REQUEST);
        }
        try {
            ScheduleDescription schedule = scheduleClient.getHandle(scheduleDTO.getScheduleId()).describe();

            Schedule.Builder updatedSchedule = Schedule.newBuilder(schedule.getSchedule());

            updatedSchedule.setState(scheduleHelper.getScheduleState(scheduleDTO.isPaused()));

            if (scheduleDTO.getCalendar() != null || scheduleDTO.getInterval() != null || scheduleDTO.getCronExpression() != null) {
                updatedSchedule.setSpec(scheduleHelper.getScheduleSpec(scheduleDTO));
            }

            updatedSchedule.setAction(scheduleHelper.getScheduleAction(scheduleDTO));


            //Check that the new schedule is valid and then delete it
            scheduleClient.createSchedule(schedule.getId()+"temp",
                    updatedSchedule.build(), ScheduleOptions.newBuilder()
                            .setTypedSearchAttributes(scheduleHelper.getSearchAttributes(scheduleDTO))
                            .build()).delete();

            //Delete old schedule
            scheduleClient.getHandle(schedule.getId()).delete();

            //Create new one with updated search attributes. Note that this may be possible to do with an Update in future temporal SDK versions
            return modelMapper.map(scheduleClient.createSchedule(schedule.getId(),
                    updatedSchedule.build(), ScheduleOptions.newBuilder()
                            .setTypedSearchAttributes(scheduleHelper.getSearchAttributes(scheduleDTO))
                            .build()
            ).describe(), ScheduleDescriptionDTO.class);

        } catch (ScheduleException e) {
            throw handleScheduleException(e, scheduleDTO.getScheduleId());
        }
    }

    /**
     * Update multiple schedules matching a given filter
     *
     * @param userId           to filter schedules by
     * @param gatewayId        to filter schedules by
     * @param schedulePatchDTO to update schedule to
     * @return ModifiedDTO containing the number of modified schedules.
     */
    public ModifiedDTO batchUpdateSchedules(Optional<String> userId, Optional<String> gatewayId, SchedulePatchDTO schedulePatchDTO) {
        if (userId.isEmpty() && gatewayId.isEmpty()) {
            throw new RequestException("Must supply at least one of 'userId' or 'gatewayId' as a query parameter", HttpStatus.BAD_REQUEST);
        }
        List<ScheduleListDescription> filteredSchedules = scheduleClient.listSchedules().filter(scheduleHelper.getStreamFilter(userId, gatewayId)).toList();
        for (ScheduleListDescription schedule : filteredSchedules) {

            String existingUserId = (String) ((List<?>) schedule.getSearchAttributes().get("userId")).get(0);
            String existingGatewayId = (String) ((List<?>) schedule.getSearchAttributes().get("gatewayId")).get(0);

            CreateScheduleDTO scheduleDetails = CreateScheduleDTO.builder()
                    .scheduleId(schedule.getScheduleId())
                    .userId(existingUserId)
                    .gatewayId(existingGatewayId)
                    .build();

            //Get existing schedule details
            Schedule.Builder updatedSchedule = Schedule.newBuilder()
                    .setState(scheduleHelper.getScheduleState(schedule.getSchedule().getState().isPaused()))
                    .setSpec(ScheduleSpec.newBuilder()
                            .setCronExpressions(schedule.getSchedule().getSpec().getCronExpressions())
                            .setIntervals(schedule.getSchedule().getSpec().getIntervals())
                            .setCalendars(schedule.getSchedule().getSpec().getCalendars())
                            .build())
                    .setAction(scheduleHelper.getScheduleAction(scheduleDetails));

            //Make updates if provided
            if (schedulePatchDTO.getPaused() != null) {
                updatedSchedule.setState(scheduleHelper.getScheduleState(schedulePatchDTO.getPaused()));
            }

            if (schedulePatchDTO.getGatewayId() != null) {
                scheduleDetails.setGatewayId(schedulePatchDTO.getGatewayId());
            }
            //Delete old schedule
            scheduleClient.getHandle(schedule.getScheduleId()).delete();

            //Create new one with updated search attributes. Note that this may be possible to do with an Update in future temporal SDK versions
            scheduleClient.createSchedule(schedule.getScheduleId(),
                    updatedSchedule.build(), ScheduleOptions.newBuilder()
                            .setTypedSearchAttributes(scheduleHelper.getSearchAttributes(scheduleDetails))
                            .build()
            );

        }

        return ModifiedDTO.builder()
                .message("Completed")
                .totalModified(filteredSchedules.size())
                .build();

    }

    /**
     * Get a schedule by ID
     *
     * @param scheduleId to get the schedule by
     * @return the schedule matching the id
     */
    public ScheduleDescriptionDTO getScheduleById(String scheduleId) {
        try {
            return modelMapper.map(scheduleClient.getHandle(scheduleId).describe(), ScheduleDescriptionDTO.class);
        } catch (ScheduleException e) {
            throw handleScheduleException(e, scheduleId);
        }
    }

    /**
     * Delete schedule by its id
     *
     * @param scheduleId of the schedule to delete
     */
    public void deleteScheduleById(String scheduleId) {
        try {
            scheduleClient.getHandle(scheduleId).delete();
        } catch (ScheduleException e) {
            throw handleScheduleException(e, scheduleId);
        }
    }

    /**
     * Get the number of schedules matching a given filter
     *
     * @param userId    to match schedules by
     * @param gatewayId to match schedules by
     * @return the number of schedules that have both userId and gatewayId
     */
    public CountDTO getScheduleCount(Optional<String> userId, Optional<String> gatewayId) {
        return CountDTO.builder()
                .total(scheduleClient.listSchedules().filter(scheduleHelper.getStreamFilter(userId, gatewayId)).count())
                .build();
    }

    /**
     * Delete multiple schedules matching the given filters
     *
     * @param userId    of the schedules to delete
     * @param gatewayId of the schedules to delete
     * @return the number of delete schedules
     */
    public ModifiedDTO deleteSchedulesByFilter(Optional<String> userId, Optional<String> gatewayId) {
        if (userId.isEmpty() && gatewayId.isEmpty()) {
            throw new RequestException("Must provide at least one of 'gatewayId' or 'userId' filters", HttpStatus.BAD_REQUEST);
        }
        List<ScheduleListDescription> filteredSchedules = scheduleClient.listSchedules().filter(scheduleHelper.getStreamFilter(userId, gatewayId)).toList();
        for (ScheduleListDescription schedule : filteredSchedules) {
            deleteScheduleById(schedule.getScheduleId());
        }
        return ModifiedDTO.builder().message("Successfully Deleted").totalModified(filteredSchedules.size()).build();
    }

    /**
     * Handles exceptions where the schedule cannot be found by its id or other issue
     *
     * @param e          the exception thrown
     * @param scheduleId the schedule ID trying to be found
     * @return request exception if the schedule cannot be found, otherwise generic runtimeException.
     */
    private RuntimeException handleScheduleException(ScheduleException e, String scheduleId) {
        log.debug(e.getMessage());
        Throwable cause = e.getCause();
        if (cause instanceof StatusRuntimeException) {
            if (Objects.equals(((StatusRuntimeException) e.getCause()).getStatus().getCode(), Status.NOT_FOUND.getCode())) {
                return new RequestException(format("Could not find Schedule with Id: %s", scheduleId), HttpStatus.NOT_FOUND);
            }
        }
        return new RuntimeException(e);
    }


}
