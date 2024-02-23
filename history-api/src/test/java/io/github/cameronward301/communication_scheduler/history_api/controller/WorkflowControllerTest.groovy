package io.github.cameronward301.communication_scheduler.history_api.controller

import io.github.cameronward301.communication_scheduler.history_api.model.TotalDTO
import io.github.cameronward301.communication_scheduler.history_api.model.WorkflowExecutionDTO
import io.github.cameronward301.communication_scheduler.history_api.service.WorkflowService
import org.springframework.data.domain.PageImpl
import org.springframework.http.HttpStatus
import spock.lang.Specification

class WorkflowControllerTest extends Specification {

    def workflowService = Mock(WorkflowService)
    WorkflowController workflowController = new WorkflowController(workflowService)

    def "Should return a 200 response when getting all workflows"() {
        given:
        Optional<String> userId = Optional.of("test-user-id")
        Optional<String> gatewayId = Optional.of("test-gateway-id")
        Optional<String> scheduleId = Optional.of("test-schedule-id")
        String taskQueue = "test-task-queue"
        String type = "test-type"
        Integer status = 2
        String pageNumber = "0"
        String pageSize = "10"

        and:
        def workflowExecutionDTO = WorkflowExecutionDTO.builder()
                .workflowId("test-workflow-id")
                .runId("test-workflow-id")
                .userId(userId.get())
                .status(status)
                .scheduleId(scheduleId.get())
                .gatewayId(gatewayId.get())
                .taskQueue(taskQueue)
                .type(type)
                .startTime(WorkflowExecutionDTO.Time.builder().seconds(0).nanos(0).build())
                .endTime(WorkflowExecutionDTO.Time.builder().seconds(10).nanos(0).build())
                .build()

        and:
        workflowService.getWorkflows(userId, gatewayId, scheduleId, status, pageNumber, pageSize) >> new PageImpl<>(List.of(workflowExecutionDTO))

        when:
        def response = workflowController.getWorkflows(userId, gatewayId, scheduleId, status, pageNumber, pageSize)

        then:
        response.getStatusCode() == HttpStatus.OK
        response.getBody().size() == 1
        response.getBody().getContent().get(0) == workflowExecutionDTO
    }

    def "Should return a 200 response when getting workflow by ID"() {
        given:
        def workflowId = "test-workflow-id"
        def runId = "test-run-id"
        Optional<String> userId = Optional.of("test-user-id")
        Optional<String> gatewayId = Optional.of("test-gateway-id")
        Optional<String> scheduleId = Optional.of("test-schedule-id")
        String taskQueue = "test-task-queue"
        String type = "test-type"
        Integer status = 2

        and:
        def workflowExecutionDTO = WorkflowExecutionDTO.builder()
                .workflowId("test-workflow-id")
                .runId("test-workflow-id")
                .userId(userId.get())
                .status(status)
                .scheduleId(scheduleId.get())
                .gatewayId(gatewayId.get())
                .taskQueue(taskQueue)
                .type(type)
                .startTime(WorkflowExecutionDTO.Time.builder().seconds(0).nanos(0).build())
                .endTime(WorkflowExecutionDTO.Time.builder().seconds(10).nanos(0).build())
                .build()

        and:
        workflowService.getWorkflowById(workflowId, runId) >> workflowExecutionDTO

        when:
        def response = workflowController.getWorkflow(workflowId, runId)

        then:
        response.getStatusCode() == HttpStatus.OK
        response.getBody() == workflowExecutionDTO
    }

    def "Should return a 204 when deleting by ID"() {
        given:
        def workflowId = "test-workflow-id"
        def runId = "test-run-id"

        and:
        workflowService.terminateWorkflow(workflowId, runId) >> null

        when:
        def response = workflowController.terminateWorkflow(workflowId, runId)

        then:
        response.getStatusCode() == HttpStatus.NO_CONTENT
    }

    def "Should return a 200 response when getting total number of workflows matching the filter"() {
        given:
        Optional<String> userId = Optional.of("test-user-id")
        Optional<String> gatewayId = Optional.of("test-gateway-id")
        Optional<String> scheduleId = Optional.of("test-schedule-id")
        Integer status = 2

        and:
        def totalDTO = TotalDTO.builder()
                .total(1234)
                .build()

        and:
        workflowService.getTotalWorkflows(userId, gatewayId, scheduleId, status) >> totalDTO

        when:
        def response = workflowController.getTotalWorkflows(userId, gatewayId, scheduleId, status)

        then:
        response.getStatusCode() == HttpStatus.OK
        response.getBody() == totalDTO
    }
}
