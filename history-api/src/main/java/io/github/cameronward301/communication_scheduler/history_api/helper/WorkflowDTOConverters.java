package io.github.cameronward301.communication_scheduler.history_api.helper;

import io.github.cameronward301.communication_scheduler.history_api.model.WorkflowExecutionDTO;
import io.temporal.client.WorkflowExecutionMetadata;
import io.temporal.common.SearchAttributeKey;
import org.springframework.stereotype.Component;

@Component
public class WorkflowDTOConverters {

    public WorkflowExecutionDTO convertToDTO(WorkflowExecutionMetadata workflowExecutionMetadata) {
        WorkflowExecutionDTO workflowExecutionDTO = WorkflowExecutionDTO.builder()
                .workflowId(workflowExecutionMetadata.getWorkflowExecutionInfo().getExecution().getWorkflowId())
                .runId(workflowExecutionMetadata.getWorkflowExecutionInfo().getExecution().getRunId())
                .userId(workflowExecutionMetadata.getTypedSearchAttributes().get(SearchAttributeKey.forKeyword("userId")))
                .gatewayId(workflowExecutionMetadata.getTypedSearchAttributes().get(SearchAttributeKey.forKeyword("gatewayId")))
                .scheduleId(workflowExecutionMetadata.getTypedSearchAttributes().get(SearchAttributeKey.forKeyword("scheduleId")))
                .type(workflowExecutionMetadata.getWorkflowType())
                .status(workflowExecutionMetadata.getStatus().getNumber())
                .taskQueue(workflowExecutionMetadata.getTaskQueue())
                .startTime(WorkflowExecutionDTO.Time.builder()
                        .seconds(workflowExecutionMetadata.getStartTime().getEpochSecond())
                        .nanos(workflowExecutionMetadata.getStartTime().getNano())
                        .build())
                .build();

        if (workflowExecutionMetadata.getCloseTime() != null) {
            workflowExecutionDTO.setEndTime(WorkflowExecutionDTO.Time.builder()
                    .seconds(workflowExecutionMetadata.getCloseTime().getEpochSecond())
                    .nanos(workflowExecutionMetadata.getCloseTime().getNano())
                    .build());
        }

        return workflowExecutionDTO;
    }
}
