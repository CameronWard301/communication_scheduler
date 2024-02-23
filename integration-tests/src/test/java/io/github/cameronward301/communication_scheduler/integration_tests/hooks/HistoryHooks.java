package io.github.cameronward301.communication_scheduler.integration_tests.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.github.cameronward301.communication_scheduler.integration_tests.world.World;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowExecutionAlreadyStarted;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.common.SearchAttributeKey;
import io.temporal.common.SearchAttributes;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class HistoryHooks {
    private final WorkflowClient workflowClient;
    private final World world;
    List<String> workflowIds = new ArrayList<>();

    public HistoryHooks(WorkflowClient workflowClient, World world) {
        this.workflowClient = workflowClient;
        this.world = world;
    }

    @SneakyThrows
    @Before("@CreateTestWorkflows")
    public void createWorkflows() {
        for (int i = 0; i < 10; i++) {
            String id = "integration-test-" + i;
            workflowIds.add(id);
            try {
                WorkflowStub workflow = getWorkflowStub(id);
                workflow.start(Map.of("id", id));
            } catch (WorkflowExecutionAlreadyStarted e) {
                WorkflowStub workflow = workflowClient.newUntypedWorkflowStub(id);
                workflow.terminate("test-termination");
                workflow = getWorkflowStub(id);
                workflow.start(Map.of("id", id));
            }

        }
        Thread.sleep(1500);

    }

    @Before("@CreateTestWorkflow")
    public void createWorkflow() throws InterruptedException {
        String id = "integration-test";
        workflowIds.add(id);
        WorkflowStub workflow = getWorkflowStub(id);
        WorkflowExecution execution = workflow.start(Map.of("id", id));
        world.setWorkflowRunId(execution.getRunId());
        world.setWorkflowId(execution.getWorkflowId());
        Thread.sleep(1500);
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
    }

    private WorkflowStub getWorkflowStub(String workflowId) {
        String GATEWAY_ID = "test-gateway-id";
        String SCHEDULE_ID = "test-schedule-id";
        String USER_ID = "test-user-id";
        return workflowClient.newUntypedWorkflowStub("CommunicationWorkflow", WorkflowOptions.newBuilder()
                .setTaskQueue("integration-test")
                .setWorkflowId(workflowId)
                .setTypedSearchAttributes(SearchAttributes.newBuilder()
                        .set(SearchAttributeKey.forKeyword("userId"), USER_ID)
                        .set(SearchAttributeKey.forKeyword("gatewayId"), GATEWAY_ID)
                        .set(SearchAttributeKey.forKeyword("scheduleId"), SCHEDULE_ID)
                        .build())
                .build());
    }
}
