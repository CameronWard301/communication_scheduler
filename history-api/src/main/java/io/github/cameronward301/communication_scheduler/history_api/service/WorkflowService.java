package io.github.cameronward301.communication_scheduler.history_api.service;

import io.github.cameronward301.communication_scheduler.history_api.exception.RequestException;
import io.github.cameronward301.communication_scheduler.history_api.helper.WorkflowDTOConverters;
import io.github.cameronward301.communication_scheduler.history_api.model.TotalDTO;
import io.github.cameronward301.communication_scheduler.history_api.model.WorkflowExecutionDTO;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowExecutionMetadata;
import io.temporal.client.WorkflowNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkflowService {

    private final WorkflowClient workflowClient;
    private final WorkflowDTOConverters workflowDTOConverters;

    public Page<WorkflowExecutionDTO> getWorkflows(
            Optional<String> userId,
            Optional<String> gatewayId,
            Optional<String> scheduleId,
            Integer status,
            String pageNumber,
            String pageSize
    ) {
        int pageNumberInt = Integer.parseInt(pageNumber);
        int pageSizeInt = Integer.parseInt(pageSize);

        List<WorkflowExecutionMetadata> filteredWorkflows = workflowClient.listExecutions(getWorkflowQueryString(userId, gatewayId, scheduleId, status)).toList();
        int totalResults = filteredWorkflows.size();


        return new PageImpl<>(filteredWorkflows.stream()
                .skip((long) pageNumberInt * pageSizeInt)
                .limit(pageSizeInt).map(workflowDTOConverters::convertToDTO)
                .collect(Collectors.toList()),
                PageRequest.of(pageNumberInt, pageSizeInt), totalResults);
    }

    public WorkflowExecutionDTO getWorkflowById(String id, String runId) {
        Optional<WorkflowExecutionMetadata> workflowExecutionDTO = workflowClient.listExecutions("WorkflowId=\"" + id + "\" AND RunId=\"" + runId + "\"")
                .findFirst();

        if (workflowExecutionDTO.isEmpty()) {
            throw new RequestException("Could not find workflow with id " + id + " and runId " + runId, HttpStatus.NOT_FOUND);
        } else {
            return workflowDTOConverters.convertToDTO(workflowExecutionDTO.get());
        }
    }

    public void terminateWorkflow(String id, String runId) {
        try {
            workflowClient.newUntypedWorkflowStub(id, Optional.of(runId), Optional.empty()).terminate("Terminated by History API");
        } catch (WorkflowNotFoundException e) {
            log.debug(e.getMessage());
            throw new RequestException("Could not find workflow with id " + id + " and runId " + runId + " to terminate", HttpStatus.NOT_FOUND);
        }
    }

    public TotalDTO getTotalWorkflows(Optional<String> userId, Optional<String> gatewayId, Optional<String> scheduleId, Integer status) {
        return TotalDTO.builder()
                .total(workflowClient.listExecutions(getWorkflowQueryString(userId, gatewayId, scheduleId, status)).count())
                .build();
    }

    private String getWorkflowQueryString(Optional<String> userId, Optional<String> gatewayId, Optional<String> scheduleId, Integer status) {
        StringBuilder builder = new StringBuilder();

        userId.ifPresent(string -> builder.append("userId=\"").append(string).append("\" AND "));
        gatewayId.ifPresent(string -> builder.append("gatewayId=\"").append(gatewayId.get()).append("\" AND "));
        scheduleId.ifPresent(string -> builder.append("scheduleId=\"").append(scheduleId.get()).append("\" AND "));

        if (status != null) {
            builder.append("ExecutionStatus=").append(status);
        }

        if (builder.toString().endsWith(" AND ")) {
            builder.setLength(builder.length() - 5);
        }

        return builder.toString();

    }

}
