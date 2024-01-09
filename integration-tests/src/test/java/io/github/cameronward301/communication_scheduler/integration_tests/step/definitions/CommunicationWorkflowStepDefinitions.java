package io.github.cameronward301.communication_scheduler.integration_tests.step.definitions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectReader;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.cameronward301.communication_scheduler.integration_tests.gateway.Email;
import io.github.cameronward301.communication_scheduler.integration_tests.gateway.Gateway;
import io.github.cameronward301.communication_scheduler.integration_tests.gateway.Sms;
import io.github.cameronward301.communication_scheduler.integration_tests.properties.TemporalProperties;
import io.github.cameronward301.communication_scheduler.integration_tests.users.EmailUser1;
import io.github.cameronward301.communication_scheduler.integration_tests.users.SmsUser1;
import io.github.cameronward301.communication_scheduler.integration_tests.users.User;
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.CommunicationWorkflow;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.api.enums.v1.WorkflowExecutionStatus;
import io.temporal.api.workflowservice.v1.DescribeWorkflowExecutionRequest;
import io.temporal.api.workflowservice.v1.DescribeWorkflowExecutionResponse;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RequiredArgsConstructor
public class CommunicationWorkflowStepDefinitions {
    private final WorkflowClient workflowClient;
    private final WorkflowServiceStubs workflowServiceStubs;
    private final TemporalProperties temporalProperties;
    private final ObjectReader objectReader;
    private final Sms sms;
    private final Email email;
    private final SmsUser1 smsUser1;
    private final EmailUser1 emailUser1;
    private User user;
    private Gateway gateway;

    private String response;


    @Given("I am user: {string}")
    public void iAmUser(String userName) {
        switch (userName) {
            case "SmsUser1":
                user = smsUser1;
                break;
            case "EmailUser1":
                user = emailUser1;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + userName);
        }
    }

    @And("Using gateway {string}")
    public void usingGateway(String gatewayName) {
        switch (gatewayName) {
            case "Sms":
                gateway = sms;
                break;
            case "Email":
                gateway = email;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + gatewayName);
        }
    }

    @When("A CommunicationWorkflow is started")
    public void aCommunicationWorkflowIsStarted() {
        CommunicationWorkflow workflow = workflowClient.newWorkflowStub(CommunicationWorkflow.class, WorkflowOptions.newBuilder()
                .setWorkflowId("intergration-test-" + user.getId())
                .setTaskQueue(temporalProperties.getTaskQueue())
                .build());

        response = workflow.sendCommunication(Map.of("userId", user.getId(), "gatewayId", gateway.getId()));
    }


    @Then("Workflow status is {WorkflowExecutionStatus}")
    public void pollWorkflowStatusUntil(WorkflowExecutionStatus status) {
        WorkflowExecution execution = WorkflowExecution.newBuilder()
                .setWorkflowId("intergration-test-" + user.getId())
                .build();

        DescribeWorkflowExecutionRequest describeWorkflowExecutionRequest = DescribeWorkflowExecutionRequest.newBuilder()
                .setNamespace(workflowClient.getOptions().getNamespace())
                .setExecution(execution)
                .build();

        DescribeWorkflowExecutionResponse describeWorkflowExecutionResponse = workflowServiceStubs.blockingStub().describeWorkflowExecution(describeWorkflowExecutionRequest);
        assert describeWorkflowExecutionResponse.getWorkflowExecutionInfo().getStatus().equals(status);
    }

    @And("Communication response is ok")
    public void communicationResponseIsOk() throws JsonProcessingException {
        Map<String, String> responseMap = objectReader.readValue(response);
        assertEquals("complete", responseMap.get("status"));
        assertEquals(user.getId(), responseMap.get("userId"));
        assertNotNull(responseMap.get("messageHash"));
    }

    @ParameterType("WORKFLOW_EXECUTION_STATUS_COMPLETED|WORKFLOW_EXECUTION_STATUS_FAILED|WORKFLOW_EXECUTION_STATUS_TIMED_OUT")
    public WorkflowExecutionStatus WorkflowExecutionStatus(String status) {
        return WorkflowExecutionStatus.valueOf(status);
    }
}
