package io.github.cameronward301.communication_scheduler.integration_tests.step.definitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.cameronward301.communication_scheduler.integration_tests.model.workflow.TotalDTO;
import io.github.cameronward301.communication_scheduler.integration_tests.model.workflow.WorkflowExecutionDTO;
import io.github.cameronward301.communication_scheduler.integration_tests.model.workflow.WorkflowListDTO;
import io.github.cameronward301.communication_scheduler.integration_tests.world.World;
import io.temporal.common.SearchAttributeKey;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;

public class HistoryAPIStepDefinitions {
    private final World world;
    private final RestTemplate restTemplate;

    private ResponseEntity<WorkflowListDTO> workflowListDTOResponseEntity;
    private ResponseEntity<WorkflowExecutionDTO> workflowExecutionDTOResponseEntity;
    private ResponseEntity<Void> deletedWorkflowResponseEntity;
    private ResponseEntity<TotalDTO> totalDTOResponseEntity;

    private String userIdFilter;
    private String gatewayIdFilter;
    private String scheduleIdFilter;
    private String workflowId;
    private String runId;
    private String pageNumber;
    private String pageSize;
    private Integer status;


    @Value("${history-api.address}")
    private String historyAPIURL;

    public HistoryAPIStepDefinitions(World world, RestTemplate restTemplate) {
        this.world = world;
        this.restTemplate = restTemplate;
    }

    @And("I set the history pageSize to be {int}")
    public void iSetTheHistoryPageSizeToBe(int pageSize) {
        this.pageSize = String.valueOf(pageSize);
    }

    @And("I set the history pageNumber to be {int}")
    public void iSetTheHistoryPageNumberToBe(int pageNumber) {
        this.pageNumber = String.valueOf(pageNumber);
    }

    @And("I set the history userId filter to be the userId")
    public void iSetTheHistoryUserIdFilterToBe() {
        assertTrue(world.getWorkflowStub().getOptions().isPresent());
        this.userIdFilter = world.getWorkflowStub().getOptions().get().getTypedSearchAttributes().get(SearchAttributeKey.forKeyword("userId"));
    }

    @And("I set the history gatewayId filter to be the gatewayId")
    public void iSetTheHistoryGatewayIdFilterToBe() {
        assertTrue(world.getWorkflowStub().getOptions().isPresent());
        this.gatewayIdFilter = world.getWorkflowStub().getOptions().get().getTypedSearchAttributes().get(SearchAttributeKey.forKeyword("gatewayId"));
    }

    @And("I set the history scheduleId filter to be the scheduleId")
    public void iSetTheHistoryScheduleIdFilterToBe() {
        assertTrue(world.getWorkflowStub().getOptions().isPresent());
        this.scheduleIdFilter = world.getWorkflowStub().getOptions().get().getTypedSearchAttributes().get(SearchAttributeKey.forKeyword("scheduleId"));

    }

    @And("I set the status filter to be {string}")
    public void iSetTheStatusFilterToBe(String status) {
        if (Objects.equals(status, "RUNNING")) {
            this.status = 1;
        }
    }

    @And("I set the workflow and run id to be from the created workflow")
    public void iSetTheWorkflowAndRunIdToBeFromTheCreatedWorkflow() {
        this.workflowId = world.getWorkflowId();
        this.runId = world.getWorkflowRunId();
    }

    @And("I set the workflow and run id to be {string}")
    public void iSetTheWorkflowAndRunIdToBe(String value) {
        this.workflowId = value;
        this.runId = value;
    }

    @When("I get all workflows")
    public void iGetAllWorkflows() throws URISyntaxException {
        try {
            workflowListDTOResponseEntity = restTemplate.exchange(getWorkflowQueryURI(List.of()), HttpMethod.GET, new HttpEntity<>(world.getHttpHeaders()), WorkflowListDTO.class);
        } catch (HttpClientErrorException e) {
            System.out.println(e.getMessage());
            world.setHttpClientErrorException(e);
        }
    }

    @When("I get get the workflow by id")
    public void iGetGetTheWorkflowById() {
        try {
            workflowExecutionDTOResponseEntity = restTemplate.exchange(historyAPIURL + "/" + workflowId + "/" + runId, HttpMethod.GET, new HttpEntity<>(world.getHttpHeaders()), WorkflowExecutionDTO.class);
        } catch (HttpClientErrorException e) {
            System.out.println(e.getMessage());
            world.setHttpClientErrorException(e);
        }
    }


    @When("I terminate the workflow by id")
    public void iTerminateTheWorkflowById() {
        try {
            deletedWorkflowResponseEntity = restTemplate.exchange(historyAPIURL + "/" + workflowId + "/" + runId, HttpMethod.DELETE, new HttpEntity<>(world.getHttpHeaders()), Void.class);
        } catch (HttpClientErrorException e) {
            System.out.println(e.getMessage());
            world.setHttpClientErrorException(e);
        }
    }

    @When("I get total workflows")
    public void iGetTotalWorkflows() throws URISyntaxException {
        try {
            totalDTOResponseEntity = restTemplate.exchange(getWorkflowQueryURI(List.of("total")), HttpMethod.GET, new HttpEntity<>(world.getHttpHeaders()), TotalDTO.class);
        } catch (HttpClientErrorException e) {
            System.out.println(e.getMessage());
            world.setHttpClientErrorException(e);
        }
    }

    @Then("the I receive a page of history workflows with a size greater than or equal to {int} and status code {int}")
    public void theIReceiveAPageOfHistoryWorkflowsWithASizeGreaterThanAndStatusCode(int size, int status) {
        assertEquals(status, workflowListDTOResponseEntity.getStatusCode().value());
        assertTrue(Objects.requireNonNull(workflowListDTOResponseEntity.getBody()).getNumberOfElements() >= size);
    }


    @Then("the I receive a page of history workflows with a size of {int} and status code {int}")
    public void theIReceiveAPageOfHistoryWorkflowsWithASizeOfAndStatusCode(int expectedSize, int statusCode) {
        assertEquals(statusCode, workflowListDTOResponseEntity.getStatusCode().value());
        assertEquals(expectedSize, Objects.requireNonNull(workflowListDTOResponseEntity.getBody()).getNumberOfElements());

        if (expectedSize == 0) {
            return;
        }

        if (userIdFilter != null) {
            assertEquals(userIdFilter, Objects.requireNonNull(workflowListDTOResponseEntity.getBody()).getContent().get(0).getUserId());
        }

        if (gatewayIdFilter != null) {
            assertEquals(gatewayIdFilter, Objects.requireNonNull(workflowListDTOResponseEntity.getBody()).getContent().get(0).getGatewayId());
        }

        if (scheduleIdFilter != null) {
            assertEquals(scheduleIdFilter, Objects.requireNonNull(workflowListDTOResponseEntity.getBody()).getContent().get(0).getScheduleId());
        }
    }

    @And("the total number of workflows matching the filter is {int}")
    public void theTotalNumberOfWorkflowsMatchingTheFilterIs(int matchingFilterNumber) {
        assertEquals(matchingFilterNumber, Objects.requireNonNull(workflowListDTOResponseEntity.getBody()).getTotalElements());
    }

    private String getWorkflowQueryURI(List<String> paths) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(historyAPIURL);
        for (String path : paths) {
            uriBuilder.appendPath(path);
        }
        if (pageNumber != null) {
            uriBuilder.addParameter("pageNumber", pageNumber);
        }
        if (pageSize != null) {
            uriBuilder.addParameter("pageSize", pageSize);
        }

        if (userIdFilter != null) {
            uriBuilder.addParameter("userId", userIdFilter);
        }

        if (gatewayIdFilter != null) {
            uriBuilder.addParameter("gatewayId", gatewayIdFilter);
        }

        if (scheduleIdFilter != null) {
            uriBuilder.addParameter("scheduleId", scheduleIdFilter);
        }

        if (status != null) {
            uriBuilder.addParameter("status", String.valueOf(status));
        }
        return uriBuilder.build().toString();
    }

    @Then("the I receive a history workflow with status code {int}")
    public void theIReceiveAHistoryWorkflowWithStatusCode(int status) {
        if (world.getHttpClientErrorException() != null) {
            System.out.println(world.getHttpClientErrorException().getMessage());
        }
        assertEquals(status, workflowExecutionDTOResponseEntity.getStatusCode().value());
        assertNotNull(workflowExecutionDTOResponseEntity.getBody());
        assertEquals(workflowId, workflowExecutionDTOResponseEntity.getBody().getWorkflowId());
        assertEquals(runId, workflowExecutionDTOResponseEntity.getBody().getRunId());
    }

    @Then("the workflow is deleted with response code {int}")
    public void theWorkflowIsDeletedWithResponseCodeAndMessage(int responseCode) {
        assertEquals(responseCode, deletedWorkflowResponseEntity.getStatusCode().value());
    }

    @Then("the response code is {int} and message is contains the workflow id and run id")
    public void theResponseCodeIsAndMessageIsContainsTheWorkflowIdAndRunId(int status) {
        assertEquals(status, world.getHttpClientErrorException().getStatusCode().value());
        assertEquals(world.getHttpClientErrorException().getMessage(), format("404 : \"Could not find workflow with id %s and runId %s to terminate\"", workflowId, runId));
    }

    @Then("the I receive a total number of {int} and status code {int}")
    public void theIReceiveATotalNumberOfAndStatusCode(int totalNumber, int statusCode) {
        assertEquals(statusCode, totalDTOResponseEntity.getStatusCode().value());
        assertEquals(totalNumber, Objects.requireNonNull(totalDTOResponseEntity.getBody()).getTotal());
    }

    @And("I set the history userId filter to be {string}")
    public void iSetTheHistoryUserIdFilterToBe(String value) {
        this.userIdFilter = value;
    }

    @And("I set the history gatewayId filter to be {string}")
    public void iSetTheHistoryGatewayIdFilterToBe(String value) {
        this.gatewayIdFilter = value;
    }

    @And("I set the history scheduleId filter to be {string}")
    public void iSetTheHistoryScheduleIdFilterToBe(String value) {
        this.scheduleIdFilter = value;
    }
}
