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

@Controller
@RequestMapping("/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PreAuthorize("hasAuthority('SCOPE_SCHEDULE:WRITE')")
    @PostMapping
    public ResponseEntity<ScheduleDescriptionDTO> createSchedule(
            @Valid @RequestBody CreateScheduleDTO createScheduleDTO,
            BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            throw new RequestException(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage(), HttpStatus.BAD_REQUEST);
        }
        if (createScheduleDTO.isInvalid()) {
            throw new RequestException("Please provide exactly one schedule configuration, either: 'calendar', 'interval' or 'cronExpression'", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(scheduleService.createSchedule(createScheduleDTO));
    }

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

    @PreAuthorize("hasAuthority('SCOPE_SCHEDULE:WRITE')")
    @PatchMapping
    public ResponseEntity<UpdateDTO> batchUpdateSchedules(
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

    @PreAuthorize("hasAuthority('SCOPE_SCHEDULE:DELETE')")
    @DeleteMapping
    public ResponseEntity<String> batchDeleteSchedules(
            @RequestParam(value = "userId", required = false) Optional<String> userId,
            @RequestParam(value = "gatewayId", required = false) Optional<String> gatewayId
    ) {
        scheduleService.deleteSchedulesByFilter(userId, gatewayId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('SCOPE_SCHEDULE:READ')")
    @GetMapping("/count")
    public ResponseEntity<CountDTO> getScheduleNumber(
            @RequestParam(value = "userId", required = false) Optional<String> userId,
            @RequestParam(value = "gatewayId", required = false) Optional<String> gatewayId
    ) {
        return ResponseEntity.ok(scheduleService.getScheduleCount(userId, gatewayId));
    }

    @PreAuthorize("hasAuthority('SCOPE_SCHEDULE:DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(
            @PathVariable String id
    ){
        scheduleService.deleteScheduleById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('SCOPE_SCHEDULE:READ')")
    @GetMapping("/{id}")
    public ResponseEntity<ScheduleDescriptionDTO> getScheduleById(
            @PathVariable String id
    ) {
        return ResponseEntity.ok(scheduleService.getScheduleById(id));
    }


}
