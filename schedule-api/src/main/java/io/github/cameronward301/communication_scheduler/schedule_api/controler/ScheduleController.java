package io.github.cameronward301.communication_scheduler.schedule_api.controler;

import io.github.cameronward301.communication_scheduler.schedule_api.exception.RequestException;
import io.github.cameronward301.communication_scheduler.schedule_api.model.*;
import io.github.cameronward301.communication_scheduler.schedule_api.service.ScheduleService;
import io.temporal.client.schedules.ScheduleListDescription;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;

/**
 * Schedule controller
 */
@Controller
@RequestMapping("/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    /**
     * Create a new schedule
     *
     * @param createScheduleDTO the schedule to create
     * @param bindingResult     validation errors of the request body
     * @return the created schedule
     */
    @PreAuthorize("hasAuthority('SCOPE_SCHEDULE:WRITE')")
    @PostMapping
    public ResponseEntity<ScheduleDescriptionDTO> createSchedule(
            @Valid @RequestBody CreateScheduleDTO createScheduleDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new RequestException(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage(), HttpStatus.BAD_REQUEST);
        }
        if (createScheduleDTO.isInvalid()) {
            throw new RequestException("Please provide exactly one schedule configuration, either: 'calendar', 'interval' or 'cronExpression'", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(scheduleService.createSchedule(createScheduleDTO), HttpStatus.CREATED);
    }

    /**
     * Update an existing schedule
     *
     * @param createScheduleDTO the new schedule parameters
     * @param bindingResult     validation errors of the DTO
     * @return the updated schedule
     */
    @PreAuthorize("hasAuthority('SCOPE_SCHEDULE:WRITE')")
    @PutMapping
    public ResponseEntity<ScheduleDescriptionDTO> updateSchedule(
            @Valid @RequestBody CreateScheduleDTO createScheduleDTO,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new RequestException(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage(), HttpStatus.BAD_REQUEST);
        }
        if (createScheduleDTO.isInvalid()) {
            throw new RequestException("Please provide exactly one schedule configuration, either: 'calendar', 'interval' or 'cronExpression'", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(scheduleService.updateSchedule(createScheduleDTO));
    }

    /**
     * Update multiple schedules matching a given filter. At least one of userId or gatewayId must be present
     *
     * @param userId           filter by userId
     * @param gatewayId        filter byGateway Id
     * @param schedulePatchDTO the details to update the schedules to
     * @param bindingResult    validation errors of the DTO
     * @return the number of modified schedules
     */
    @PreAuthorize("hasAuthority('SCOPE_SCHEDULE:WRITE')")
    @PatchMapping
    public ResponseEntity<ModifiedDTO> batchUpdateSchedules(
            @RequestParam(value = "userId", required = false) Optional<String> userId,
            @RequestParam(value = "gatewayId", required = false) Optional<String> gatewayId,
            @Valid @RequestBody SchedulePatchDTO schedulePatchDTO,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new RequestException(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage(), HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(scheduleService.batchUpdateSchedules(userId, gatewayId, schedulePatchDTO));
    }

    /**
     * Get schedules matching a given filter
     *
     * @param pageNumber page number of request
     * @param pageSize   number of resources per page
     * @param userId     to filter schedules by
     * @param gatewayId  to filter schedules by
     * @return page of schedules matching the filter
     */
    @PreAuthorize("hasAuthority('SCOPE_SCHEDULE:READ')")
    @GetMapping
    public ResponseEntity<Page<ScheduleListDescription>> getSchedules(
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") String pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "25") String pageSize,
            @RequestParam(value = "userId", required = false) Optional<String> userId,
            @RequestParam(value = "gatewayId", required = false) Optional<String> gatewayId
    ) {
        return ResponseEntity.ok(scheduleService.getAllSchedules(pageNumber, pageSize, userId, gatewayId));
    }

    /**
     * Delete schedules matching the given filter, note that at least one of userId or gatewayId must be supplied
     *
     * @param userId    to filter schedules by
     * @param gatewayId to filter schedules by
     * @return the number of deleted schedules
     */
    @PreAuthorize("hasAuthority('SCOPE_SCHEDULE:DELETE')")
    @DeleteMapping
    public ResponseEntity<ModifiedDTO> batchDeleteSchedules(
            @RequestParam(value = "userId", required = false) Optional<String> userId,
            @RequestParam(value = "gatewayId", required = false) Optional<String> gatewayId
    ) {

        return ResponseEntity.ok(scheduleService.deleteSchedulesByFilter(userId, gatewayId));
    }

    /**
     * Get the number of schedules matching a given filter
     *
     * @param userId    to filter schedules by
     * @param gatewayId to filter schedules by
     * @return the number of schedules matching the given filter
     */
    @PreAuthorize("hasAuthority('SCOPE_SCHEDULE:READ')")
    @GetMapping("/count")
    public ResponseEntity<CountDTO> getScheduleNumber(
            @RequestParam(value = "userId", required = false) Optional<String> userId,
            @RequestParam(value = "gatewayId", required = false) Optional<String> gatewayId
    ) {
        return ResponseEntity.ok(scheduleService.getScheduleCount(userId, gatewayId));
    }

    /**
     * Delete a schedule by id
     *
     * @param id of schedule to delete
     * @return no content
     */
    @PreAuthorize("hasAuthority('SCOPE_SCHEDULE:DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(
            @PathVariable String id
    ) {
        scheduleService.deleteScheduleById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * get a schedule by id
     *
     * @param id of schedule to get
     * @return the schedule with the specified id
     */
    @PreAuthorize("hasAuthority('SCOPE_SCHEDULE:READ')")
    @GetMapping("/{id}")
    public ResponseEntity<ScheduleDescriptionDTO> getScheduleById(
            @PathVariable String id
    ) {
        return ResponseEntity.ok(scheduleService.getScheduleById(id));
    }


}
