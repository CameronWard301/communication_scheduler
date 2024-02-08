package io.github.cameronward301.communication_scheduler.stress_test.component;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.CommunicationWorkflow;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

@Component
@RequiredArgsConstructor
@Slf4j
public class WorkflowStarter implements ApplicationRunner {

    private final WorkflowClient client;

    @Value("${workflow.number}")
    private int numberOfWorkflows;

    @Value("${workflow.gatewayId}")
    private String gatewayId;

    @Value("${workflow.userId}")
    private String userId;

    @Value("${worker.taskQueue}")
    private String taskQueue;



    @Override
    public void run(ApplicationArguments args) {
        List<WorkflowExecution> workflowExecutions = new ArrayList<>();

        log.info("Starting workflows");
        for (int i = 0; i < numberOfWorkflows; i++) {
            CommunicationWorkflow workflow = client.newWorkflowStub(CommunicationWorkflow.class,
                    WorkflowOptions.newBuilder()
                            .setTaskQueue(taskQueue)
                            .setWorkflowId(format("%s:%s:%s:%s",gatewayId, userId, "stress-test", i + 1))
                            .build());

            workflowExecutions.add(WorkflowClient.start(workflow::sendCommunication, Map.of("userId", userId, "gatewayId", gatewayId)));
        }
        log.info("Started {} workflows", numberOfWorkflows);

        log.info("Waiting for workflows to complete");
        for (WorkflowExecution workflowExecution : workflowExecutions) {
            WorkflowStub workflowStub = client.newUntypedWorkflowStub(workflowExecution.getWorkflowId());
            workflowStub.getResult(JsonNode.class);
        }
        log.info("Workflows completed");

    }
}
