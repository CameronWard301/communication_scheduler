package io.github.cameronward301.communication_scheduler.integration_tests.step.definitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.cameronward301.communication_scheduler.integration_tests.gateway.Gateway;
import io.github.cameronward301.communication_scheduler.integration_tests.model.schedule.ScheduleEntity;
import io.github.cameronward301.communication_scheduler.integration_tests.world.World;
import io.temporal.client.schedules.ScheduleClient;
import io.temporal.common.SearchAttributeKey;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;

import java.text.DateFormat;
import java.text.ParseException;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import static io.github.cameronward301.communication_scheduler.integration_tests.step.definitions.SharedWebPortalStepDefinitions.setTextField;
import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SchedulePageStepDefinitions {
    private final WebDriver webDriver;
    private final ScheduleClient scheduleClient;
    private final World world;

    private final ScheduleEntity scheduleEntity;
    private final Gateway existingGateway;

    @Value("${web-portal.explicit-wait}")
    private int explicitWait;

    @Value("${web-portal.address}")
    private String webDriverUrl;

    public SchedulePageStepDefinitions(WebDriver webDriver, ScheduleClient scheduleClient, World world, ScheduleEntity scheduleEntity, Gateway existingGateway) {
        this.webDriver = webDriver;
        this.scheduleClient = scheduleClient;
        this.world = world;
        this.scheduleEntity = scheduleEntity;
        this.existingGateway = existingGateway;
    }

    @When("I set the {string} to be the schedule id")
    public void iSetTheFieldToBeTheScheduleId(String id) {
        setTextField(webDriver, id, scheduleEntity.getScheduleId());
    }

    @When("I set the {string} to be the user id")
    public void iSetTheToBeTheUserId(String userId) {
        setTextField(webDriver, userId, scheduleEntity.getScheduleOptions().getTypedSearchAttributes().get(SearchAttributeKey.forKeyword("userId")));
    }

    @When("I {string} the schedule by id")
    public void iPauseTheScheduleById(String action) {
        webDriver.findElement(By.id(action + "-schedule-" + scheduleEntity.getScheduleId())).click();

    }


    @Then("I should see the schedule")
    public void iShouldSeeTheSchedule() throws ParseException {
        Wait<WebDriver> wait = new WebDriverWait(webDriver, Duration.ofSeconds(explicitWait));
        wait.until(ExpectedConditions.textMatches(By.cssSelector(".MuiDataGrid-cell:nth-child(3)"), Pattern.compile(scheduleEntity.getScheduleId())));
        assertThat(webDriver.findElement(By.cssSelector(".MuiDataGrid-cell:nth-child(3)")).getText(), is(scheduleEntity.getScheduleId()));
        assert scheduleEntity.getSchedule().getState() != null;
        assertThat(webDriver.findElement(By.cssSelector(".MuiDataGrid-cell:nth-child(4)")).getText(), is(scheduleEntity.getSchedule().getState().isPaused() ? "Paused" : "Running"));
        assertThat(webDriver.findElement(By.cssSelector(".MuiDataGrid-cell:nth-child(5)")).getText(), is(existingGateway.getFriendlyName()));
        assertThat(webDriver.findElement(By.cssSelector(".MuiDataGrid-cell:nth-child(6)")).getText(), is(scheduleEntity.getScheduleOptions().getTypedSearchAttributes().get(SearchAttributeKey.forKeyword("gatewayId"))));
        assertThat(webDriver.findElement(By.cssSelector(".MuiDataGrid-cell:nth-child(7)")).getText(), is(scheduleEntity.getScheduleOptions().getTypedSearchAttributes().get(SearchAttributeKey.forKeyword("userId"))));
        DateFormat dateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        assertTrue(dateFormat.parse(webDriver.findElement(By.cssSelector(".MuiDataGrid-cell:nth-child(8)")).getText().replace(",", "")).after(new Date()));
    }

    @And("the total schedule results should be {int}")
    public void theTotalScheduleResultsShouldBe(int total) {
        Wait<WebDriver> wait = new WebDriverWait(webDriver, Duration.ofSeconds(explicitWait));
        wait.until(ExpectedConditions.textMatches(By.cssSelector(".MuiTablePagination-displayedRows"), Pattern.compile(format("%s–%s of %s", total, total, total))));
    }

    @And("the total schedule results should be {string}")
    public void theTotalScheduleResultsShouldBe(String total) {
        Wait<WebDriver> wait = new WebDriverWait(webDriver, Duration.ofSeconds(explicitWait));
        wait.until(ExpectedConditions.textMatches(By.cssSelector(".MuiTablePagination-displayedRows"), Pattern.compile(total)));
    }


    @Then("I should see the confirm schedule modal")
    public void iShouldSeeTheConfirmScheduleModal() {
        Wait<WebDriver> wait = new WebDriverWait(webDriver, Duration.ofSeconds(explicitWait));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("modify-schedule-id")));
        assertThat(webDriver.findElement(By.id("modify-schedule-id")).getText(), is(scheduleEntity.getScheduleId()));
        assertThat(webDriver.findElement(By.id("modify-gateway-friendly-name")).getText(), is(existingGateway.getFriendlyName()));
        assertThat(webDriver.findElement(By.id("modify-user-id")).getText(), is(scheduleEntity.getScheduleOptions().getTypedSearchAttributes().get(SearchAttributeKey.forKeyword("userId"))));
        List<WebElement> elements = webDriver.findElements(By.cssSelector("#modify-schedule > div"));
        assertThat(elements.size(), is(3));
    }

    @Then("I should see the schedule status as {string}")
    public void iShouldSeeTheScheduleStatusAs(String status) {
        Wait<WebDriver> wait = new WebDriverWait(webDriver, Duration.ofSeconds(explicitWait));
        wait.until(ExpectedConditions.textMatches(By.cssSelector(".MuiDataGrid-cell:nth-child(4)"), Pattern.compile(status)));
    }

    @Then("The URI is set to edit the schedule")
    public void theURIIsSetToEditTheSchedule() {
        assertEquals(webDriverUrl + "/schedule/" + scheduleEntity.getScheduleId(), webDriver.getCurrentUrl());
    }

    @And("I can see the edit schedule page")
    public void iCanSeeTheEditSchedulePage() throws ParseException {
        assertThat(webDriver.findElement(By.id("schedule-edit-page-heading")).getText(), is("Edit Communication Schedule"));
        assertThat(webDriver.findElement(By.id("schedule-id")).getText(), is("ID: " + scheduleEntity.getScheduleId()));
        assertTrue(webDriver.findElement(By.id("date-created")).getText().contains("Date Created: ") && webDriver.findElement(By.id("date-created")).getText().length() > 17);
        assertThat(webDriver.findElement(By.id("status")).getText(), is("Status: Running"));
        assertThat(webDriver.findElement(By.id("user-id-input")).getAttribute("value"), is(scheduleEntity.getScheduleOptions().getTypedSearchAttributes().get(SearchAttributeKey.forKeyword("userId"))));
        assertThat(webDriver.findElement(By.id("gateway-id")).getText(), is(scheduleEntity.getScheduleOptions().getTypedSearchAttributes().get(SearchAttributeKey.forKeyword("gatewayId"))));
        assertThat(webDriver.findElement(By.id("gateway-friendly-name")).getText(), is(existingGateway.getFriendlyName()));
        assertThat(webDriver.findElement(By.id("gateway-endpoint-url")).getText(), is(existingGateway.getEndpointUrl()));
        assertThat(webDriver.findElement(By.id("gateway-description")).getText(), is(existingGateway.getDescription()));
        DateFormat dateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        assertTrue(dateFormat.parse(webDriver.findElement(By.id("gateway-date-created")).getText().replace(",", "")).before(new Date()));
        List<WebElement> elements = webDriver.findElements(By.cssSelector("#current-schedule > li"));
        assertThat(elements.size(), is(7));
    }

    @When("I navigate go to the edit schedule page")
    public void iNavigateGoToTheEditSchedulePage() {
        webDriver.navigate().to(webDriverUrl + "/schedule/" + scheduleEntity.getScheduleId());
    }

    @And("I search for the new gateway")
    public void iSearchForTheNewGateway() {
        setTextField(webDriver, "gateway-id-filter-input", existingGateway.getId() + "-1");
    }

    @Then("I should see the new gateway details for gateway {string} with id prefix {string}")
    public void iShouldSeeTheNewGatewayDetails(String gatewayNumber, String idPrefix) {
        assertEquals(existingGateway.getId() + "-" + gatewayNumber, webDriver.findElement(By.id(idPrefix + "gateway-id")).getText());
        assertEquals("test gateway " + gatewayNumber + " name", webDriver.findElement(By.id(idPrefix + "gateway-friendly-name")).getText());
        assertEquals("https://test-gateway-" + gatewayNumber + ".com/monthly/newsletter", webDriver.findElement(By.id(idPrefix + "gateway-endpoint-url")).getText());
        assertEquals("test gateway " + gatewayNumber + " description", webDriver.findElement(By.id(idPrefix + "gateway-description")).getText());
    }

    @Then("I should see the existing gateway details")
    public void iShouldSeeTheExistingGatewayDetails() {
        assertEquals(existingGateway.getId(), webDriver.findElement(By.id("gateway-id")).getText());
        assertEquals("test gateway name", webDriver.findElement(By.id("gateway-friendly-name")).getText());
        assertEquals("https://test-gateway.com/monthly/newsletter", webDriver.findElement(By.id("gateway-endpoint-url")).getText());
        assertEquals("test gateway description", webDriver.findElement(By.id("gateway-description")).getText());
    }

    @Then("I should see the old and new gateway confirm modal for gateway {string}")
    public void iShouldSeeTheOldAndNewGatewayConfirmModal(String gatewayNumber) {
        assertEquals("Are you sure you want to modify this schedule?", webDriver.findElement(By.id("transition-modal-title")).getText());
        assertEquals("ID: " + existingGateway.getId(), webDriver.findElement(By.id("old-gateway-id")).getText());
        assertEquals("Friendly Name: " + existingGateway.getFriendlyName(), webDriver.findElement(By.id("old-gateway-friendly-name")).getText());
        assertEquals(existingGateway.getId() + "-1", webDriver.findElement(By.id("new-gateway-id")).getText());
        assertEquals("test gateway " + gatewayNumber + " name", webDriver.findElement(By.id("new-gateway-friendly-name")).getText());

    }

    @And("I should see the list with id {string} with {int} results")
    public void iCanSeeTheUpcomingScheduleWithResults(String id, int resultNumber) {
        List<WebElement> elements = webDriver.findElements(By.cssSelector("#" + id + " > li"));
        assertThat(elements.size(), is(resultNumber));
    }

    @And("I save the user ID for the created schedule as {string}")
    public void iSaveTheUserIDForTheCreatedSchedule(String userId) {
        world.setCreatedScheduleUserId(userId);
    }

    @When("I search for the existing gateway")
    public void iSearchForTheExistingGateway() {
        setTextField(webDriver, "gateway-id-filter-input", existingGateway.getId());
    }

    @When("I click the first schedule in the data grid filter results")
    public void iClickTheFirstScheduleInTheDataGridFilterResults() {
        webDriver.findElement(By.cssSelector(".MuiDataGrid-row:nth-child(1) .PrivateSwitchBase-input")).click();
    }

    @When("I press the select all button in the data grid")
    public void iPressTheSelectAllButtonInTheDataGrid() {
        webDriver.findElement(By.cssSelector(".MuiDataGrid-checkboxInput > .PrivateSwitchBase-input")).click();
    }

    @And("the selected schedules now have a status of {string}")
    public void theSelectedSchedulesNowHaveAStatusOf(String value) {
        Wait<WebDriver> wait = new WebDriverWait(webDriver, Duration.ofSeconds(explicitWait));
        wait.until(ExpectedConditions.textMatches(By.cssSelector(".MuiDataGrid-cell:nth-child(4)"), Pattern.compile(value)));
        assertThat(webDriver.findElement(By.cssSelector(".MuiDataGrid-row--firstVisible .MuiTypography-root")).getText(), is(value));
        assertThat(webDriver.findElement(By.cssSelector(".MuiDataGrid-row--lastVisible .MuiTypography-root")).getText(), is(value));
    }

    @And("the first schedule has a status of {string}")
    public void theFirstScheduleHasAStatusOf(String value) {
        assertThat(webDriver.findElement(By.cssSelector(".MuiDataGrid-row--firstVisible .MuiTypography-root")).getText(), is(value));
    }

    @And("the last schedule has a status of {string}")
    public void theLastScheduleHasAStatusOf(String value) {
        assertThat(webDriver.findElement(By.cssSelector(".MuiDataGrid-row--lastVisible .MuiTypography-root")).getText(), is(value));
    }
}
