package io.github.cameronward301.communication_scheduler.integration_tests.step.definitions;

import io.cucumber.java.ParameterType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.cameronward301.communication_scheduler.integration_tests.gateway.Email;
import io.github.cameronward301.communication_scheduler.integration_tests.gateway.GatewayType;
import io.github.cameronward301.communication_scheduler.integration_tests.gateway.Sms;
import io.github.cameronward301.communication_scheduler.integration_tests.properties.IntegrationTestTemporalProperties;
import io.github.cameronward301.communication_scheduler.integration_tests.users.EmailUser1;
import io.github.cameronward301.communication_scheduler.integration_tests.users.SmsUser1;
import io.github.cameronward301.communication_scheduler.integration_tests.users.User;
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.CommunicationWorkflow;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.api.enums.v1.WorkflowExecutionStatus;
import io.temporal.api.workflowservice.v1.DescribeWorkflowExecutionRequest;
import io.temporal.api.workflowservice.v1.DescribeWorkflowExecutionResponse;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowFailedException;
import io.temporal.client.WorkflowOptions;
import io.temporal.failure.ApplicationFailure;
import io.temporal.serviceclient.WorkflowServiceStubs;
import lombok.RequiredArgsConstructor;
import org.hamcrest.CoreMatchers;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RequiredArgsConstructor
public class CommunicationWorkflowStepDefinitions {
    private final WorkflowClient workflowClient;
    private final WorkflowServiceStubs workflowServiceStubs;
    private final IntegrationTestTemporalProperties integrationTestTemporalProperties;
    private final Sms sms;
    private final Email email;
    private final SmsUser1 smsUser1;
    private final EmailUser1 emailUser1;
    private User user;
    private GatewayType gatewayType;
    private Instant workflowStartTime;

    private Map<String, String> response;
    private Duration workflowExecutionTimeout = Duration.ofSeconds(15);
    private WorkflowFailedException workflowFailedException;


    @Given("I am user: {string}")
    public void iAmUser(String userName) {
        switch (userName) {
            case "SmsUser1" -> user = smsUser1;
            case "EmailUser1" -> user = emailUser1;
            case "Unknown" -> user = new User() {
                @Override
                public String getId() {
                    return super.getId();
                }
            };
            default -> throw new IllegalStateException("Unexpected value: " + userName);
        }
    }

    @And("Using gateway {string}")
    public void usingGateway(String gatewayName) {
        switch (gatewayName) {
            case "Sms" -> gatewayType = sms;

            case "Email" -> gatewayType = email;
            case "Unknown" -> gatewayType = new GatewayType() {
                @Override
                public String getId() {
                    return super.getId();
                }
            };
            default -> throw new IllegalStateException("Unexpected value: " + gatewayName);
        }
    }

    @And("Workflow timeout is {int} seconds")
    public void workflowTimeoutIsSeconds(int seconds) {
        workflowExecutionTimeout = Duration.ofSeconds(seconds);
    }

    @When("A CommunicationWorkflow is started")
    public void aCommunicationWorkflowIsStarted() {
        workflowStartTime = Instant.now();
        CommunicationWorkflow workflow = workflowClient.newWorkflowStub(CommunicationWorkflow.class, WorkflowOptions.newBuilder()
                .setWorkflowId(gatewayType.getId() + ":" + user.getId() + ":intergration-test:-" + workflowStartTime.toString())
                .setTaskQueue(integrationTestTemporalProperties.getTaskQueue())
                .setWorkflowExecutionTimeout(workflowExecutionTimeout)
                .build());

        try {
            response = workflow.sendCommunication(Map.of("userId", user.getId(), "gatewayId", gatewayType.getId()));
        } catch (WorkflowFailedException e) {
            workflowFailedException = e;
        }
    }


    @Then("Workflow status is {WorkflowExecutionStatus}")
    public void pollWorkflowStatusUntil(WorkflowExecutionStatus status) {
        WorkflowExecution execution = WorkflowExecution.newBuilder()
                .setWorkflowId(gatewayType.getId() + ":" + user.getId() + ":intergration-test:-" + workflowStartTime.toString())
                .build();

        DescribeWorkflowExecutionRequest describeWorkflowExecutionRequest = DescribeWorkflowExecutionRequest.newBuilder()
                .setNamespace(workflowClient.getOptions().getNamespace())
                .setExecution(execution)
                .build();

        DescribeWorkflowExecutionResponse describeWorkflowExecutionResponse = workflowServiceStubs.blockingStub().describeWorkflowExecution(describeWorkflowExecutionRequest);
        assertEquals(status, describeWorkflowExecutionResponse.getWorkflowExecutionInfo().getStatus());
    }

    @And("Communication response is ok")
    public void communicationResponseIsOk() {
        assertEquals("complete", response.get("status"));
        assertEquals(user.getId(), response.get("userId"));
        assertNotNull(response.get("messageHash"));
    }

    @ParameterType("WORKFLOW_EXECUTION_STATUS_COMPLETED|WORKFLOW_EXECUTION_STATUS_FAILED|WORKFLOW_EXECUTION_STATUS_TIMED_OUT")
    public WorkflowExecutionStatus WorkflowExecutionStatus(String status) {
        return WorkflowExecutionStatus.valueOf(status);
    }

    @And("Communication response is Status: {int}")
    public void communicationResponseIsStatus(int status) {
        ApplicationFailure applicationFailure = (ApplicationFailure) workflowFailedException.getCause().getCause();
        assertThat(applicationFailure.getMessage(), CoreMatchers.containsString("Gateway unsuccessful, status: " + status));

    }

    @And("Application failure message contains: {string}")
    public void applicationFailureMessage(String message) {
        ApplicationFailure applicationFailure = (ApplicationFailure) workflowFailedException.getCause().getCause();
        assertThat(applicationFailure.getMessage(), CoreMatchers.containsString(message));

    }
}
