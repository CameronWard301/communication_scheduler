package io.github.cameronward301.communication_scheduler.integration_tests.step.definitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor
public class SharedWebPortalStepDefinitions {
    private final WebDriver webDriver;
    @Value("${web-portal.address}")
    private String webDriverUrl;

    @Value("${web-portal.explicit-wait}")
    private int explicitWait;

    private static void clearTextField(WebDriver driver, String id) {
        driver.findElement(By.id(id)).sendKeys(Keys.CONTROL + "a");
        driver.findElement(By.id(id)).sendKeys(Keys.BACK_SPACE);
    }

    public static void setTextField(WebDriver webDriver, String id, String value) {
        clearTextField(webDriver, id);
        webDriver.findElement(By.id(id)).sendKeys(value);
    }

    public static void setNumericField(WebDriver webDriver, String id, String value) {
        clearTextField(webDriver, id);
        webDriver.findElement(By.id(id)).sendKeys(value);
        webDriver.findElement(By.id(id)).sendKeys(Keys.HOME);
        webDriver.findElement(By.id(id)).sendKeys(Keys.DELETE);
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

    @And("I press enter on the field with id {string}")
    public void iPressEnter(String id) {
        webDriver.findElement(By.id(id)).sendKeys(Keys.ENTER);
    }

    @Then("the field with id {string} should be set to: {string}")
    public void theFieldWithIdShouldBeSetTo(String fieldId, String value) {
        assertEquals(value, webDriver.findElement(By.id(fieldId)).getAttribute("value"));

    }

    @Then("the element with id {string} should be set to: {string}")
    public void theElementWithIdShouldBeSetTo(String fieldId, String value) {
        assertEquals(value, webDriver.findElement(By.id(fieldId)).getText());

    }

    @Then("I should see a snackbar message with the text {string}")
    public void iShouldSeeASnackbarMessageWithTheText(String message) {
        WebElement element = webDriver.findElement(By.id("snackbar-message"));
        Wait<WebDriver> wait = new WebDriverWait(webDriver, Duration.ofSeconds(explicitWait));
        wait.until(driver -> element.isDisplayed());
        assertThat(element.getText(), is(message));
    }

    @When("I set the {string} field to be {string}")
    public void iSetTheFieldToBe(String id, String value) {
        setTextField(webDriver, id, value);
    }

    @Then("Wait for snackbar to disappear")
    public void waitForSnackbarToDisappear() {
        WebElement element = webDriver.findElement(By.id("snackbar-message"));
        Wait<WebDriver> wait = new WebDriverWait(webDriver, Duration.ofSeconds(explicitWait * 10L));
        wait.until(ExpectedConditions.invisibilityOf(element));
    }

    @Then("the button with id {string} should be disabled")
    public void theButtonWithIdShouldBeDisabled(String id) {
        WebElement element = webDriver.findElement(By.id(id));
        Wait<WebDriver> wait = new WebDriverWait(webDriver, Duration.ofSeconds(explicitWait));
        wait.until(driver -> !element.isEnabled());
    }

    @Then("the button with id {string} should not be disabled")
    public void theButtonWithIdShouldNotBeDisabled(String id) {
        WebElement element = webDriver.findElement(By.id(id));
        Wait<WebDriver> wait = new WebDriverWait(webDriver, Duration.ofSeconds(explicitWait));
        wait.until(driver -> element.isEnabled());
    }


}
