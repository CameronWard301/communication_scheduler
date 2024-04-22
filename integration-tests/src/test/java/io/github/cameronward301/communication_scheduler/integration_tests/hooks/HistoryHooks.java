package io.github.cameronward301.communication_scheduler.integration_tests.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.github.cameronward301.communication_scheduler.integration_tests.gateway.Gateway;
import io.github.cameronward301.communication_scheduler.integration_tests.model.schedule.ScheduleEntity;
import io.github.cameronward301.communication_scheduler.integration_tests.world.World;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.api.enums.v1.WorkflowExecutionStatus;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowExecutionAlreadyStarted;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.common.SearchAttributeKey;
import io.temporal.common.SearchAttributes;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class HistoryHooks {
    private final WorkflowClient workflowClient;
    private final World world;
    private final Gateway gateway;
    private final ScheduleEntity schedule;
    List<String> workflowIds = new ArrayList<>();

    public HistoryHooks(WorkflowClient workflowClient, World world, @Qualifier("gatewayDbModel") Gateway gateway, @Qualifier("scheduleEntity") ScheduleEntity scheduleEntity) {
        this.workflowClient = workflowClient;
        this.world = world;
        this.gateway = gateway;
        this.schedule = scheduleEntity;
    }

    @Before(value = "@TerminateExistingWorkflows", order = 1)
    public void terminateExistingWorkflowsBeforeAll() {
        workflowClient.listExecutions("").filter(workflowExecution -> workflowExecution.getExecution().getWorkflowId().contains("integration-test")).forEach(workflowExecution -> {
            try {
                WorkflowStub workflow = workflowClient.newUntypedWorkflowStub(workflowExecution.getExecution().getWorkflowId());
                workflow.terminate("test-termination");
                checkWorkflowIsTerminated(workflowExecution.getExecution().getWorkflowId());
            } catch (Exception e) {
                log.debug(e.getMessage());
            }
        });

    }

    @SneakyThrows
    @Before(value = "@CreateTestWorkflows", order = 2)
    public void createWorkflows() {
        for (int i = 0; i < 10; i++) {
            String id = "integration-test-" + i;
            workflowIds.add(id);
            try {
                WorkflowStub workflow = getWorkflowStub(id);
                workflow.start(Map.of("id", id));
                world.setWorkflowStub(workflow);
            } catch (WorkflowExecutionAlreadyStarted e) {
                WorkflowStub workflow = workflowClient.newUntypedWorkflowStub(id);
                workflow.terminate("test-termination");
                workflow = getWorkflowStub(id);
                world.setWorkflowStub(workflow);
                workflow.start(Map.of("id", id));
            }

        }
        checkWorkflowsAreCreated();

    }

    @Before(value = "@CreateTestWorkflow", order = 2)
    public void createWorkflow() {
        String id = "integration-test";
        workflowIds.add(id);
        WorkflowStub workflow = getWorkflowStub(id);
        WorkflowExecution execution = workflow.start(Map.of("id", id));
        world.setWorkflowRunId(execution.getRunId());
        world.setWorkflowId(execution.getWorkflowId());
        world.setWorkflowExecution(execution);
        world.setWorkflowStub(workflow);
        checkWorkflowsAreCreated();
    }

    @After("@RemoveTestWorkflows")
    public void removeWorkflows() {
        for (String workflowId : workflowIds) {
            try {
                WorkflowStub workflow = workflowClient.newUntypedWorkflowStub(workflowId);
                workflow.terminate("test-termination");
            } catch (Exception e) {
                log.debug(e.getMessage());
            }
        }
        checkWorkflowsAreTerminated();
    }

    private WorkflowStub getWorkflowStub(String workflowId) {
        return workflowClient.newUntypedWorkflowStub("CommunicationWorkflow", WorkflowOptions.newBuilder()
                .setTaskQueue("integration-test")
                .setWorkflowId(workflowId)
                .setTypedSearchAttributes(SearchAttributes.newBuilder()
                        .set(SearchAttributeKey.forKeyword("userId"), schedule.getScheduleOptions().getTypedSearchAttributes().get(SearchAttributeKey.forKeyword("userId")))
                        .set(SearchAttributeKey.forKeyword("gatewayId"), gateway.getId())
                        .set(SearchAttributeKey.forKeyword("scheduleId"), schedule.getScheduleId())
                        .build())
                .build());
    }

    private void checkWorkflowsAreCreated() {
        for (String id : workflowIds) {
            int attempts = 10;
            while (attempts > 0) {
                if (workflowClient.listExecutions("WorkflowId=\"" + id + "\"").filter(workflowExecutionMetadata -> workflowExecutionMetadata.getStatus()== WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_RUNNING).count() == 1) {
                    return;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    log.error("Interrupted while waiting for workflow to start", e);
                    throw new RuntimeException("Interrupted while waiting for workflow to start");
                }
                attempts--;
            }
            throw new RuntimeException("Workflow with id: " + id + " did not reach running state");
        }
    }

    private void checkWorkflowsAreTerminated() {
        for (String id : workflowIds) {
            checkWorkflowIsTerminated(id);
        }
    }

    private void checkWorkflowIsTerminated(String id){
        int attempts = 10;
        while (attempts > 0) {
            if (workflowClient.listExecutions("WorkflowId=\"" + id + "\"").noneMatch(workflowExecutionMetadata -> workflowExecutionMetadata.getStatus() == WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_RUNNING)) {
                return;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                log.error("Interrupted while waiting for workflow to terminate", e);
                throw new RuntimeException("Interrupted while waiting for workflow to terminate");
            }
            attempts--;
        }
        throw new RuntimeException("Workflow with id: " + id + " did not reach terminated state");
    }
}
