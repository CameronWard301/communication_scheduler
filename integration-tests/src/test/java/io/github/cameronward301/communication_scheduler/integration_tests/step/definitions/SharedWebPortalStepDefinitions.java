package io.github.cameronward301.communication_scheduler.integration_tests.step.definitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import org.hamcrest.CoreMatchers;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

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
        Wait<WebDriver> wait = new WebDriverWait(webDriver, Duration.ofSeconds(explicitWait));
        wait.until(ExpectedConditions.elementToBeClickable(By.id(element)));
        webDriver.findElement(By.id(element)).click();
    }

    @Then("The URI is now {string}")
    public void theURIIsNow(String uri) {
        Wait<WebDriver> wait = new WebDriverWait(webDriver, Duration.ofSeconds(explicitWait));
        wait.until(ExpectedConditions.urlToBe(webDriverUrl + uri));
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
        Wait<WebDriver> wait = new WebDriverWait(webDriver, Duration.ofSeconds(explicitWait));
        wait.until(ExpectedConditions.textToBe(By.id(fieldId), (value)));
        assertEquals(value, webDriver.findElement(By.id(fieldId)).getText());
    }

    @Then("I should see a snackbar message with the text {string}")
    public void iShouldSeeASnackbarMessageWithTheText(String message) {
        Wait<WebDriver> wait = new FluentWait<>(webDriver)
                .withTimeout(Duration.ofSeconds(explicitWait))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(StaleElementReferenceException.class);

        wait.until((driver -> {
            WebElement element = driver.findElement(By.id("snackbar-message"));
            return element.getText().equals(message);
        }));

        WebElement element = webDriver.findElement(By.id("snackbar-message"));
        assertThat(element.getText(), is(message));
    }

    @When("I set the {string} field to be {string}")
    public void iSetTheFieldToBe(String id, String value) {
        setTextField(webDriver, id, value);
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


    @And("I wait {int} second")
    public void iWaitSecond(int time) {
        try {
            Thread.sleep(time * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Then("I close the snackbar")
    public void iCloseTheSnackbar() {
        webDriver.findElement(By.cssSelector("#snackbar-message > .MuiAlert-action > .MuiIconButton-colorInherit")).click();
    }

    @And("the field with id {string} is not: {string}")
    public void theFieldWithIdIsNot(String elementId, String value) {
        assertNotSame(webDriver.findElement(By.id(elementId)).getText(), value);
    }

    @When("I press DEL by name on {string}")
    public void iClickByInputNameOn(String identifier) {
        WebElement element = webDriver.findElement(By.name(identifier));
        element.click();
        element.sendKeys(Keys.DELETE);
    }

    @When("I send the keys {string} to the field with name {string}")
    public void iSendTheKeysToTheFieldWithId(String keys, String element) {
        WebElement webElement = webDriver.findElement(By.name(element));
        webElement.sendKeys(keys);
    }

    @And("the element with css class {string} should be set to: {string}")
    public void theElementWithCssClassShouldBeSetTo(String cssClass, String value) {
        Wait<WebDriver> wait = new WebDriverWait(webDriver, Duration.ofSeconds(explicitWait));
        wait.until(ExpectedConditions.textToBe(By.className(cssClass), (value)));
        assertEquals(value, webDriver.findElement(By.className(cssClass)).getText());
    }

    @And("the element with css class {string} should be: {string}")
    public void theElementWithCssClassShouldContain(String cssClass, String value) {
        Wait<WebDriver> wait = new WebDriverWait(webDriver, Duration.ofSeconds(explicitWait));
        wait.until(ExpectedConditions.textToBe(By.className(cssClass), value));
        assertEquals(value, webDriver.findElement(By.className(cssClass)).getText());
    }

    @And("the element with id {string} should be set to: {string} after clicking by id on {string}")
    public void theElementWithIdShouldBeSetToAfterClickingByIdOn(String testId, String value, String refreshId) {
        Wait<WebDriver> wait = new FluentWait<>(webDriver)
                .withTimeout(Duration.ofSeconds(explicitWait))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class);

        wait.until((driver -> {
            driver.findElement(By.id(refreshId)).click();
            assertThat(webDriver.findElement(By.id(testId)).getText(), is(value));
            return ExpectedConditions.textToBe(By.id(testId), value);
        }));
    }
}
