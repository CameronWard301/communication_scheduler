package io.github.cameronward301.communication_scheduler.integration_tests.step.definitions;

import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.cameronward301.communication_scheduler.integration_tests.model.JwtDTO;
import io.github.cameronward301.communication_scheduler.integration_tests.world.World;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SharedStepDefinitions {

    private final World world;
    private final RestTemplate restTemplate;

    private final WebDriver webDriver;

    @Value("${auth-api.address}")
    private String authAPIUrl;

    @Value("${web-portal.address}")
    private String webDriverUrl;

    @Value("${web-portal.explicit-wait}")
    private int explicitWait;

    public SharedStepDefinitions(World world, RestTemplate restTemplate, WebDriver webDriver) {
        this.world = world;
        this.restTemplate = restTemplate;
        this.webDriver = webDriver;
    }

    @And("I have a bearer token with the {string} scope")
    public void iHaveABearerTokenWithTheScope(String scope) {
        HttpHeaders httpHeaders = world.getHttpHeaders();
        String authToken = Objects.requireNonNull(restTemplate.postForEntity(authAPIUrl, List.of(scope), JwtDTO.class).getBody()).getToken();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("Authorization", "Bearer " + authToken);
        world.setHttpHeaders(httpHeaders);
    }

    @And("I have no token")
    public void iHaveNoToken() {
        HttpHeaders httpHeaders = world.getHttpHeaders();
        if (httpHeaders.get("Authorization") != null) {
            httpHeaders.remove("Authorization");
        }
    }

    @Then("the response code is {int} and message: {string}")
    public void theResponseCodeIsAndMessage(int code, String message) {
        assertEquals(code, world.getHttpClientErrorException().getStatusCode().value());
        assertEquals(message, world.getHttpClientErrorException().getMessage());
    }

    @When("I navigate to {string}")
    public void iNavigateTo(String uri) {
        webDriver.get(webDriverUrl + uri);
    }

    @And("I click by id on {string}")
    public void iClickByIdOn(String element) {
        webDriver.findElement(By.id(element)).click();
    }

    @Then("The URI is now {string}")
    public void theURIIsNow(String uri) {
        assertEquals(webDriverUrl + uri, webDriver.getCurrentUrl());

    }

    @Then("the field with id {string} should be set to: {string}")
    public void theFieldWithIdShouldBeSetTo(String fieldId, String value) {
        assertEquals(value, webDriver.findElement(By.id(fieldId)).getAttribute("value"));

    }

    @Then("I should see a snackbar message with the text {string}")
    public void iShouldSeeASnackbarMessageWithTheText(String message) {
        WebElement element = webDriver.findElement(By.id("snackbar-message"));
        Wait<WebDriver> wait = new WebDriverWait(webDriver, Duration.ofSeconds(explicitWait));
        wait.until(driver -> element.isDisplayed());
        assertThat(element.getText(), is(message));
    }


    @Then("Wait for snackbar to disappear")
    public void waitForSnackbarToDisappear() {
        WebElement element = webDriver.findElement(By.id("snackbar-message"));
        Wait<WebDriver> wait = new WebDriverWait(webDriver, Duration.ofSeconds(explicitWait * 10L));
        wait.until(ExpectedConditions.invisibilityOf(element));
    }
}
