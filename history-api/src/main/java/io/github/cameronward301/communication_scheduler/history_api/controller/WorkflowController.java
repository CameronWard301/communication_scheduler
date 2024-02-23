package io.github.cameronward301.communication_scheduler.history_api.controller;

import io.github.cameronward301.communication_scheduler.history_api.model.TotalDTO;
import io.github.cameronward301.communication_scheduler.history_api.model.WorkflowExecutionDTO;
import io.github.cameronward301.communication_scheduler.history_api.service.WorkflowService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@AllArgsConstructor
@RequestMapping("/workflow")
public class WorkflowController {

    private final WorkflowService workflowService;

    @GetMapping("")
    @PreAuthorize("hasAuthority('SCOPE_HISTORY:READ')")
    public ResponseEntity<Page<WorkflowExecutionDTO>> getWorkflows(
            @RequestParam(name = "userId", required = false) Optional<String> userId,
            @RequestParam(name = "gatewayId", required = false) Optional<String> gatewayId,
            @RequestParam(name = "scheduleId", required = false) Optional<String> scheduleId,
            @RequestParam(name = "status", required = false) Integer status,
            @RequestParam(name = "pageNumber", required = false, defaultValue = "0") String pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = "50") String pageSize
    ) {
        return ResponseEntity.ok(workflowService.getWorkflows(userId, gatewayId, scheduleId, status, pageNumber, pageSize));
    }

    @GetMapping("/{workflowId}/{runId}")
    @PreAuthorize("hasAuthority('SCOPE_HISTORY:READ')")
    public ResponseEntity<WorkflowExecutionDTO> getWorkflow(
            @PathVariable String workflowId,
            @PathVariable String runId
    ) {
        return ResponseEntity.ok(workflowService.getWorkflowById(workflowId, runId));
    }

    @DeleteMapping("/{workflowId}/{runId}")
    @PreAuthorize("hasAuthority('SCOPE_WORKFLOW:TERMINATE')")
    public ResponseEntity<Void> terminateWorkflow(
            @PathVariable String workflowId,
            @PathVariable String runId
    ) {
        workflowService.terminateWorkflow(workflowId, runId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/total")
    @PreAuthorize("hasAuthority('SCOPE_HISTORY:READ')")
    public ResponseEntity<TotalDTO> getTotalWorkflows(
            @RequestParam(name = "userId", required = false) Optional<String> userId,
            @RequestParam(name = "gatewayId", required = false) Optional<String> gatewayId,
            @RequestParam(name = "scheduleId", required = false) Optional<String> scheduleId,
            @RequestParam(name = "status", required = false) Integer status
    ) {
        return ResponseEntity.ok(workflowService.getTotalWorkflows(userId, gatewayId, scheduleId, status));
    }
}
