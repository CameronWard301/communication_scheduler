package io.github.cameronward301.communication_scheduler.integration_tests.step.definitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.cameronward301.communication_scheduler.integration_tests.gateway.Gateway;
import io.github.cameronward301.communication_scheduler.integration_tests.world.World;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;

import java.text.DateFormat;
import java.text.ParseException;
import java.time.Duration;
import java.util.Date;
import java.util.regex.Pattern;

import static io.github.cameronward301.communication_scheduler.integration_tests.step.definitions.SharedWebPortalStepDefinitions.setTextField;
import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GatewayPageStepDefinitions {

    private final WebDriver webDriver;
    private final String existingGatewayId;
    private final Gateway existingGateway;
    private final World world;


    @Value("${web-portal.explicit-wait}")
    private int explicitWait;

    public GatewayPageStepDefinitions(WebDriver webDriver, Gateway gateway, World world) {
        this.webDriver = webDriver;
        this.existingGatewayId = gateway.getId();
        this.existingGateway = gateway;
        this.world = world;

    }


    @When("I set the {string} to be the gateway id")
    public void iSetTheFieldToBeTheGatewayId(String id) {
        setTextField(webDriver, id, existingGatewayId);
    }

    @When("I set the {string} to be the gateway name")
    public void iSetTheToBeTheGatewayName(String id) {
        setTextField(webDriver, id, existingGateway.getFriendlyName());
    }

    @When("I set the {string} to be the gateway description")
    public void iSetTheToBeTheGatewayDescription(String id) {
        setTextField(webDriver, id, existingGateway.getDescription());
    }

    @When("I set the {string} to be the gateway endpoint url")
    public void iSetTheToBeTheGatewayEndpointUrl(String id) {
        setTextField(webDriver, id, existingGateway.getEndpointUrl());
    }

    @Then("I should see the gateway with the id")
    public void iShouldSeeTheGatewayWithTheId() throws ParseException {
        Wait<WebDriver> wait = new WebDriverWait(webDriver, Duration.ofSeconds(explicitWait * 10L));
        wait.until(ExpectedConditions.textMatches(By.cssSelector(".MuiDataGrid-cell:nth-child(2)"), Pattern.compile(existingGatewayId)));
        assertThat(webDriver.findElement(By.cssSelector(".MuiDataGrid-cell:nth-child(2)")).getText(), is(existingGatewayId));
        assertThat(webDriver.findElement(By.cssSelector(".MuiDataGrid-cell:nth-child(3)")).getText(), is(existingGateway.getFriendlyName()));
        assertThat(webDriver.findElement(By.cssSelector(".MuiDataGrid-cell:nth-child(4)")).getText(), is(existingGateway.getDescription()));
        assertThat(webDriver.findElement(By.cssSelector(".MuiDataGrid-cell:nth-child(5)")).getText(), is(existingGateway.getEndpointUrl()));
        DateFormat dateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        assertTrue(dateFormat.parse(webDriver.findElement(By.cssSelector(".MuiDataGrid-cell:nth-child(6)")).getText().replace(",", "")).before(new Date()));
    }

    @Then("I should see the gateway with the name")
    public void iShouldSeeTheGatewayWithTheName() {
        Wait<WebDriver> wait = new WebDriverWait(webDriver, Duration.ofSeconds(explicitWait * 10L));
        wait.until(ExpectedConditions.textMatches(By.cssSelector(".MuiDataGrid-cell:nth-child(3)"), Pattern.compile(existingGateway.getFriendlyName())));
    }

    @Then("I should see the gateway with the description")
    public void iShouldSeeTheGatewayWithTheDescription() {
        Wait<WebDriver> wait = new WebDriverWait(webDriver, Duration.ofSeconds(explicitWait * 10L));
        wait.until(ExpectedConditions.textMatches(By.cssSelector(".MuiDataGrid-cell:nth-child(4)"), Pattern.compile(existingGateway.getDescription())));

    }

    @Then("I should see the gateway with the endpoint url")
    public void iShouldSeeTheGatewayWithTheEndpointUrl() {
        Wait<WebDriver> wait = new WebDriverWait(webDriver, Duration.ofSeconds(explicitWait * 10L));
        wait.until(ExpectedConditions.textMatches(By.cssSelector(".MuiDataGrid-cell:nth-child(5)"), Pattern.compile(existingGateway.getEndpointUrl())));

    }

    @And("the total gateway results should be {int}")
    public void theTotalGatewayResultsShouldBe(int total) {
        assertThat(webDriver.findElement(By.cssSelector(".MuiTablePagination-displayedRows")).getText(), is(format("%s–%s of %s", total, total, total)));

    }

    @Then("I should see the gateway with the name, description and endpoint url")
    public void iShouldSeeTheGatewayWithTheNameDescriptionAndEndpointUrl() throws ParseException {
        iShouldSeeTheGatewayWithTheId();
    }

    @And("I click the modify gateway button")
    public void iClickTheModifyGatewayButton() {
        webDriver.findElement(By.id("modify-gateway-" + existingGatewayId)).click();
    }

    @When("I click the delete gateway button")
    public void iClickTheDeleteGatewayButton() {
        webDriver.findElement(By.id("delete-gateway-" + existingGatewayId)).click();
    }

    @Then("the delete gateway fields are shown")
    public void theDeleteGatewayFieldsAreShown() {
        Wait<WebDriver> wait = new WebDriverWait(webDriver, Duration.ofSeconds(explicitWait * 10L));
        wait.until(ExpectedConditions.textMatches(By.id("transition-modal-title"), Pattern.compile("Delete Gateway")));
        assertThat(webDriver.findElement(By.id("transition-modal-title")).getText(), is("Delete Gateway"));
        assertThat(webDriver.findElement(By.id("gateway-id")).getText(), is(existingGatewayId));
        assertThat(webDriver.findElement(By.id("gateway-friendly-name")).getText(), is(existingGateway.getFriendlyName()));
        assertThat(webDriver.findElement(By.id("gateway-endpoint-url")).getText(), is(existingGateway.getEndpointUrl()));
        assertThat(webDriver.findElement(By.id("gateway-description")).getText(), is(existingGateway.getDescription()));
    }

    @Then("the add gateway fields are shown")
    public void theAddGatewayFieldsAreShown() {
        Wait<WebDriver> wait = new WebDriverWait(webDriver, Duration.ofSeconds(explicitWait * 10L));
        wait.until(ExpectedConditions.textMatches(By.id("transition-modal-title"), Pattern.compile("Add Gateway")));
        assertThat(webDriver.findElement(By.id("transition-modal-title")).getText(), is("Add Gateway"));
        assertThat(webDriver.findElement(By.id("gateway-friendly-name")).getText(), is(world.getGateway().getFriendlyName()));
        assertThat(webDriver.findElement(By.id("gateway-endpoint-url")).getText(), is(world.getGateway().getEndpointUrl()));
        assertThat(webDriver.findElement(By.id("gateway-description")).getText(), is(world.getGateway().getDescription()));
    }

    @And("I set the input fields to be the gateway information")
    public void iSetTheInputFieldsToBeTheGatewayInformation() {
        setTextField(webDriver, "gateway-name-input", world.getGateway().getFriendlyName());
        setTextField(webDriver, "gateway-url-input", world.getGateway().getEndpointUrl());
        setTextField(webDriver, "gateway-description-input", world.getGateway().getDescription());
    }

    @And("the field with id {string} should be set to the created gateway id")
    public void theFieldWithIdShouldBeSetToTheCreatedGatewayId(String fieldId) {
        assertNotEquals("", webDriver.findElement(By.id(fieldId)).getAttribute("value"));
        Gateway gateway = world.getGateway();
        gateway.setId(webDriver.findElement(By.id(fieldId)).getAttribute("value"));
        world.setGateway(gateway);
    }
}
