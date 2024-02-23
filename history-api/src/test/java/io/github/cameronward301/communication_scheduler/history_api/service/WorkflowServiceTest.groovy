package io.github.cameronward301.communication_scheduler.history_api.service


import com.google.protobuf.Timestamp
import io.github.cameronward301.communication_scheduler.history_api.exception.RequestException
import io.github.cameronward301.communication_scheduler.history_api.helper.WorkflowDTOConverters
import io.github.cameronward301.communication_scheduler.history_api.model.TotalDTO
import io.temporal.api.common.v1.WorkflowExecution
import io.temporal.api.common.v1.WorkflowType
import io.temporal.api.enums.v1.WorkflowExecutionStatus
import io.temporal.api.workflow.v1.WorkflowExecutionInfo
import io.temporal.client.WorkflowClient
import io.temporal.client.WorkflowExecutionMetadata
import io.temporal.client.WorkflowNotFoundException
import io.temporal.client.WorkflowStub
import io.temporal.common.converter.DataConverter
import org.springframework.http.HttpStatus
import spock.lang.Specification

import java.util.stream.Stream

class WorkflowServiceTest extends Specification {
    def workflowClient = Mock(WorkflowClient)
    def dataConverter = Mock(DataConverter)
    def workflowService = new WorkflowService(workflowClient, new WorkflowDTOConverters())

    def "Should get workflows"() {
        given:
        def userId = Optional.of("test-user-id")
        def gatewayId = Optional.of("test-gateway-id")
        def scheduleId = Optional.of("test-schedule-id")
        def workflowId = "test-workflow-id"
        def runId = "test-run-id"
        def type = "test-type"
        def taskQueue = "test-task-queue"
        def startTime = Timestamp.newBuilder().setSeconds(10).setNanos(20).build()
        def endTime = Timestamp.newBuilder().setSeconds(30).setNanos(40).build()
        def status = 2
        def pageNumber = "0"
        def pageSize = "1"

        and:
        workflowClient.listExecutions("userId=\"test-user-id\" AND gatewayId=\"test-gateway-id\" AND scheduleId=\"test-schedule-id\" AND ExecutionStatus=2") >> {
            //noinspection GroovyAccessibility
            Stream.of(new WorkflowExecutionMetadata(
                    WorkflowExecutionInfo.newBuilder()
                            .setExecution(WorkflowExecution.newBuilder()
                                    .setWorkflowId(workflowId)
                                    .setRunId(runId).build())
                            .setStatus(WorkflowExecutionStatus.forNumber(status))
                            .setStartTime(startTime)
                            .setCloseTime(endTime)
                            .setTaskQueue(taskQueue)
                            .setType(WorkflowType.newBuilder().setName(type).build())
                            .build(),
                    dataConverter
            ))
        }

        when:
        def response = workflowService.getWorkflows(userId, gatewayId, scheduleId, status, pageNumber, pageSize)

        then:
        response.size() == 1
        response.getTotalPages() == 1
        response.getPageable().getPageSize() == Integer.valueOf(pageSize)
        response.getPageable().getPageNumber() == Integer.valueOf(pageNumber)
        response.getContent().get(0).getWorkflowId() == workflowId
        response.getContent().get(0).getRunId() == runId
        response.getContent().get(0).getType() == type
        response.getContent().get(0).getTaskQueue() == taskQueue
        response.getContent().get(0).getStartTime().getSeconds() == startTime.getSeconds()
        response.getContent().get(0).getStartTime().getNanos() == startTime.getNanos()
        response.getContent().get(0).getEndTime().getSeconds() == endTime.getSeconds()
        response.getContent().get(0).getEndTime().getNanos() == endTime.getNanos()
        response.getContent().get(0).getStatus() == status
    }

    def "Should get workflow by id"() {
        given:
        def workflowId = "test-workflow-id"
        def runId = "test-run-id"
        def type = "test-type"
        def taskQueue = "test-task-queue"
        def startTime = Timestamp.newBuilder().setSeconds(10).setNanos(20).build()
        def endTime = Timestamp.newBuilder().setSeconds(30).setNanos(40).build()
        def status = 2


        and:
        workflowClient.listExecutions("WorkflowId=\"test-workflow-id\" AND RunId=\"test-run-id\"") >> {
            //noinspection GroovyAccessibility
            Stream.of(new WorkflowExecutionMetadata(
                    WorkflowExecutionInfo.newBuilder()
                            .setExecution(WorkflowExecution.newBuilder()
                                    .setWorkflowId(workflowId)
                                    .setRunId(runId).build())
                            .setStatus(WorkflowExecutionStatus.forNumber(status))
                            .setStartTime(startTime)
                            .setCloseTime(endTime)
                            .setTaskQueue(taskQueue)
                            .setType(WorkflowType.newBuilder().setName(type).build())
                            .build(),
                    dataConverter
            ))
        }

        when:
        def response = workflowService.getWorkflowById(workflowId, runId)

        then:
        response.getWorkflowId() == workflowId
        response.getRunId() == runId
        response.getType() == type
        response.getTaskQueue() == taskQueue
        response.getStartTime().getSeconds() == startTime.getSeconds()
        response.getStartTime().getNanos() == startTime.getNanos()
        response.getEndTime().getSeconds() == endTime.getSeconds()
        response.getEndTime().getNanos() == endTime.getNanos()
        response.getStatus() == status
    }

    def "Should throw exception if can't find workflowId"() {
        given:
        def workflowId = "test-workflow-id"
        def runId = "test-run-id"

        and:
        workflowClient.listExecutions("WorkflowId=\"test-workflow-id\" AND RunId=\"test-run-id\"") >> Stream.of()


        when:
        workflowService.getWorkflowById(workflowId, runId)

        then:
        def exception = thrown(RequestException)
        exception.getMessage() == "Could not find workflow with id " + workflowId + " and runId " + runId
        exception.getHttpStatus() == HttpStatus.NOT_FOUND
    }

    def "Should delete workflow with no exception"() {
        given:
        def workflowId = "test-workflow-id"
        def runId = "test-run-id"

        and:
        def workflowStub = Mock(WorkflowStub)
        workflowClient.newUntypedWorkflowStub(workflowId, Optional.of(runId), Optional.empty()) >> workflowStub
        workflowStub.terminate(_ as String) >> null


        when:
        workflowService.terminateWorkflow(workflowId, runId)

        then:
        notThrown(Exception)
    }

    def "Should throw exception if can't delete by workflowId"() {
        given:
        def workflowId = "test-workflow-id"
        def runId = "test-run-id"

        and:
        workflowClient.newUntypedWorkflowStub(workflowId, Optional.of(runId), Optional.empty()) >> { //noinspection GroovyAccessibility
            throw new WorkflowNotFoundException(new WorkflowExecution(), "test-type", new Throwable("test exception"))
        }


        when:
        workflowService.terminateWorkflow(workflowId, runId)

        then:
        def exception = thrown(RequestException)
        exception.getMessage() == "Could not find workflow with id " + workflowId + " and runId " + runId + " to terminate"
        exception.getHttpStatus() == HttpStatus.NOT_FOUND
    }


    def "Should get total workflows"() {
        given:
        def userId = Optional.of("test-user-id")
        def gatewayId = Optional.of("test-gateway-id")
        def scheduleId = Optional.of("test-schedule-id")
        def workflowId = "test-workflow-id"
        def runId = "test-run-id"
        def type = "test-type"
        def taskQueue = "test-task-queue"
        def startTime = Timestamp.newBuilder().setSeconds(10).setNanos(20).build()
        def endTime = Timestamp.newBuilder().setSeconds(30).setNanos(40).build()
        def status = 2

        and:
        workflowClient.listExecutions("userId=\"test-user-id\" AND gatewayId=\"test-gateway-id\" AND scheduleId=\"test-schedule-id\" AND ExecutionStatus=2") >> {
            //noinspection GroovyAccessibility
            Stream.of(new WorkflowExecutionMetadata(
                    WorkflowExecutionInfo.newBuilder()
                            .setExecution(WorkflowExecution.newBuilder()
                                    .setWorkflowId(workflowId)
                                    .setRunId(runId).build())
                            .setStatus(WorkflowExecutionStatus.forNumber(status))
                            .setStartTime(startTime)
                            .setCloseTime(endTime)
                            .setTaskQueue(taskQueue)
                            .setType(WorkflowType.newBuilder().setName(type).build())
                            .build(),
                    dataConverter
            ))
        }

        when:
        def response = workflowService.getTotalWorkflows(userId, gatewayId, scheduleId, status)

        then:
        response.getTotal() == 1
    }

    def "Should get total workflows by userId produces correct query string"() {
        given:
        def userId = Optional.of("test-user-id")
        def workflowId = "test-workflow-id"
        def runId = "test-run-id"
        def type = "test-type"
        def taskQueue = "test-task-queue"
        def startTime = Timestamp.newBuilder().setSeconds(10).setNanos(20).build()
        def endTime = Timestamp.newBuilder().setSeconds(30).setNanos(40).build()
        def status = 2

        and:
        workflowClient.listExecutions("userId=\"test-user-id\"") >> {
            //noinspection GroovyAccessibility
            Stream.of(new WorkflowExecutionMetadata(
                    WorkflowExecutionInfo.newBuilder()
                            .setExecution(WorkflowExecution.newBuilder()
                                    .setWorkflowId(workflowId)
                                    .setRunId(runId).build())
                            .setStatus(WorkflowExecutionStatus.forNumber(status))
                            .setStartTime(startTime)
                            .setCloseTime(endTime)
                            .setTaskQueue(taskQueue)
                            .setType(WorkflowType.newBuilder().setName(type).build())
                            .build(),
                    dataConverter
            ))
        }

        when:
        def response = workflowService.getTotalWorkflows(userId, Optional.empty(), Optional.empty(), null)

        then:
        response == TotalDTO.builder().total(1).build()
    }

}
