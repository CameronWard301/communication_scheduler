package io.github.cameronward301.communication_scheduler.integration_tests.step.definitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.cameronward301.communication_scheduler.integration_tests.gateway.Gateway;
import io.github.cameronward301.communication_scheduler.integration_tests.world.World;
import io.temporal.common.SearchAttributeKey;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.util.regex.Pattern;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HistoryPageStepDefinitions {
    private final WebDriver webDriver;
    private final World world;
    private final Gateway gateway;

    @Value("${web-portal.explicit-wait}")
    private int explicitWait;

    @Value("${web-portal.address}")
    private String webDriverUrl;

    public HistoryPageStepDefinitions(WebDriver webDriver, World world, Gateway gateway) {
        this.webDriver = webDriver;
        this.world = world;
        this.gateway = gateway;
    }


    @Then("the total history results should be {string}")
    public void theTotalHistoryResultsShouldBe(String total) {
        Wait<WebDriver> wait = new WebDriverWait(webDriver, Duration.ofSeconds(explicitWait));
        wait.until(ExpectedConditions.textMatches(By.cssSelector(".MuiTablePagination-displayedRows"), Pattern.compile(total)));

    }

    @And("the status cell of the first item should be set to {string}")
    public void theStatusCellOfTheFirstItemShouldBeSetTo(String value) {
        Wait<WebDriver> wait = new WebDriverWait(webDriver, Duration.ofSeconds(explicitWait));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector(".MuiDataGrid-row:nth-child(1) .MuiDataGrid-cell:nth-child(2)"), value));
        assert webDriver.findElement(By.cssSelector(".MuiDataGrid-row:nth-child(1) .MuiDataGrid-cell:nth-child(2")).getText().equals(value);
    }

    @And("I set the status filter to {string}")
    public void iSetTheStatusFilterTo(String status) {
        webDriver.findElement(By.id("status-filter-button")).click();
        webDriver.findElement(By.id(status)).click();
        webDriver.findElement(By.id("status-filter-button")).click();
    }

    @And("the correct history item should be displayed")
    public void theCorrectHistoryItemShouldBeDisplayed() {
        Wait<WebDriver> wait = new WebDriverWait(webDriver, Duration.ofSeconds(explicitWait));
        wait.until(ExpectedConditions.textMatches(By.cssSelector(".MuiDataGrid-cell:nth-child(3)"), Pattern.compile(world.getWorkflowRunId())));
        assertThat(webDriver.findElement(By.cssSelector(".MuiDataGrid-cell:nth-child(3)")).getText(), is(world.getWorkflowExecution().getRunId()));
        assertThat(webDriver.findElement(By.cssSelector(".MuiDataGrid-cell:nth-child(2)")).getText(), is("Running"));
        assertTrue(world.getWorkflowStub().getOptions().isPresent());
        assertThat(webDriver.findElement(By.cssSelector(".MuiDataGrid-cell:nth-child(4)")).getText(), is(world.getWorkflowStub().getOptions().get().getTypedSearchAttributes().get(SearchAttributeKey.forKeyword("userId"))));
        assertThat(webDriver.findElement(By.cssSelector(".MuiDataGrid-cell:nth-child(5)")).getText(), is(gateway.getFriendlyName()));
        assertThat(webDriver.findElement(By.cssSelector(".MuiDataGrid-cell:nth-child(6)")).getText(), is(world.getWorkflowStub().getOptions().get().getTypedSearchAttributes().get(SearchAttributeKey.forKeyword("scheduleId"))));
        assertNotNull(webDriver.findElement(By.cssSelector(".MuiDataGrid-cell:nth-child(7)")).getText());
    }

    @Given("I navigate to the history page with filters correctly set")
    public void iNavigateToTheHistoryPageWithFiltersCorrectlySet() {
        assertTrue(world.getWorkflowStub().getOptions().isPresent());
        webDriver.navigate().to(webDriverUrl + "/history?status=Running&gatewayId=" + gateway.getId() + "&userId=" + world.getWorkflowStub().getOptions().get().getTypedSearchAttributes().get(SearchAttributeKey.forKeyword("userId")) + "&scheduleId=" + world.getWorkflowStub().getOptions().get().getTypedSearchAttributes().get(SearchAttributeKey.forKeyword("scheduleId"))+ "&scheduleId=" + world.getWorkflowStub().getOptions().get().getTypedSearchAttributes().get(SearchAttributeKey.forKeyword("scheduleId")));
    }

    @When("I click view on the history item")
    public void iClickViewOnTheHistoryItem() {
        webDriver.findElement(By.id("view-communication-"+world.getWorkflowRunId())).click();
    }

    @Then("the history item should be displayed correctly with running status {string}")
    public void theHistoryItemShouldBeDisplayedCorrectly(String runningStatus) {
        checkWorkflowDetails();
        assertThat(webDriver.findElement(By.id("communication-status")).getText(), is(runningStatus));
    }

    @When("I click stop on the history item")
    public void iClickStopOnTheHistoryItem() {
        webDriver.findElement(By.id("stop-communication-"+world.getWorkflowRunId())).click();
    }

    @Then("I see the confirm stop modal")
    public void iSeeTheConfirmStopModal() {
        checkWorkflowDetails();
        assertThat(webDriver.findElement(By.id("stop-com-transition-modal-title")).getText(), is("Are you sure you want to stop this communication?"));
    }

    private void checkWorkflowDetails() {
        Wait<WebDriver> wait = new WebDriverWait(webDriver, Duration.ofSeconds(explicitWait));
        wait.until(ExpectedConditions.textMatches(By.id("communication-run-id"), Pattern.compile(world.getWorkflowRunId())));
        assertTrue(world.getWorkflowStub().getOptions().isPresent());
        assertThat(webDriver.findElement(By.id("communication-run-id")).getText(), is(world.getWorkflowExecution().getRunId()));
        assertThat(webDriver.findElement(By.id("communication-user-id")).getText(), is(world.getWorkflowStub().getOptions().get().getTypedSearchAttributes().get(SearchAttributeKey.forKeyword("userId"))));
        assertThat(webDriver.findElement(By.id("communication-gateway-id")).getText(), is(gateway.getId()));
        assertThat(webDriver.findElement(By.id("communication-gateway-name")).getText(), is(gateway.getFriendlyName()));
        assertThat(webDriver.findElement(By.id("communication-schedule-id")).getText(), is(world.getWorkflowStub().getOptions().get().getTypedSearchAttributes().get(SearchAttributeKey.forKeyword("scheduleId"))));


    }

    @And("the endTime cell should be set to a valid date")
    public void theEndTimeCellShouldBeSetToAValidDate() {
        assertNotNull(webDriver.findElement(By.cssSelector(".MuiDataGrid-cell:nth-child(8)")).getText());
    }

    @Then("the url should contain the gateway id filter")
    public void theUrlShouldContainTheGatewayIdFilter() {
        webDriver.switchTo().window(webDriver.getWindowHandles().stream().toList().get(1));
        assertThat(webDriver.getCurrentUrl(), is(webDriverUrl + "/gateways?gatewayId=" + gateway.getId()));
    }

    @Then("the url should contain the schedule id filter")
    public void theUrlShouldContainTheScheduleIdFilter() {
        webDriver.switchTo().window(webDriver.getWindowHandles().stream().toList().get(1));
        assertTrue( world.getWorkflowStub().getOptions().isPresent());
        assertThat(webDriver.getCurrentUrl(), is(webDriverUrl + "/schedules?scheduleId=" + world.getWorkflowStub().getOptions().get().getTypedSearchAttributes().get(SearchAttributeKey.forKeyword("scheduleId"))));
    }
}
