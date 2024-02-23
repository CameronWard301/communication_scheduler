package io.github.cameronward301.communication_scheduler.history_api.model

import spock.lang.Specification

class WorkflowExecutionDTOTest extends Specification {

    def "Should create dto from no args"() {
        when:
        def workflowExecutionDTO = new WorkflowExecutionDTO()

        then:
        workflowExecutionDTO != null
    }

    def "Should create dto from builder"() {
        given:
        def workflowId = "test-workflow-id"
        def runId = "test-run-id"
        def userId = "test-user-id"
        def gatewayId = "test-gateway-id"
        def scheduleId = "test-schedule-id"
        def taskQueue = "test-task-queue"
        def type = "test-type"
        def status = 2
        def startTime = WorkflowExecutionDTO.Time.builder().seconds(0).nanos(0).build()
        def endTime = WorkflowExecutionDTO.Time.builder().seconds(10).nanos(0).build()

        when:
        def workflowExecutionDTO = WorkflowExecutionDTO.builder()
                .workflowId(workflowId)
                .runId(runId)
                .userId(userId)
                .status(status)
                .scheduleId(scheduleId)
                .gatewayId(gatewayId)
                .taskQueue(taskQueue)
                .type(type)
                .startTime(startTime)
                .endTime(endTime)
                .build()

        then:
        workflowExecutionDTO.getWorkflowId() == workflowId
        workflowExecutionDTO.getRunId() == runId
        workflowExecutionDTO.getUserId() == userId
        workflowExecutionDTO.getStatus() == status
        workflowExecutionDTO.getScheduleId() == scheduleId
        workflowExecutionDTO.getGatewayId() == gatewayId
        workflowExecutionDTO.getTaskQueue() == taskQueue
        workflowExecutionDTO.getType() == type
        workflowExecutionDTO.getStartTime() == startTime
        workflowExecutionDTO.getEndTime() == endTime
    }
}
