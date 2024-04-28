package io.github.cameronward301.communication_scheduler.integration_tests.step.definitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.github.cameronward301.communication_scheduler.integration_tests.gateway.Gateway;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GatewayFilterComponentStepDefinitions {
    private final WebDriver webDriver;
    private final Gateway existingGateway;


    @Value("${web-portal.explicit-wait}")
    private int explicitWait;

    public GatewayFilterComponentStepDefinitions(WebDriver webDriver, Gateway existingGateway) {
        this.webDriver = webDriver;
        this.existingGateway = existingGateway;
    }

    @Then("I should see the gateway with the id in the filter table")
    public void iShouldSeeTheGatewayWithTheId() {
        Wait<WebDriver> wait = new FluentWait<>(webDriver)
                .withTimeout(Duration.ofSeconds(explicitWait))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .ignoring(AssertionError.class);
        wait.until((driver -> {
            driver.findElement(By.id("gateway-filter-search")).click();
            assertThat(driver.findElement((By.cssSelector(".MuiDataGrid-cell:nth-child(3)"))).getText(), is(existingGateway.getId()));
            return driver.findElement((By.cssSelector(".MuiDataGrid-cell:nth-child(3)")));
        }));
        assertThat(webDriver.findElement(By.cssSelector(".MuiDataGrid-cell:nth-child(3)")).getText(), is(existingGateway.getId()));
        assertThat(webDriver.findElement(By.cssSelector(".MuiDataGrid-cell:nth-child(4)")).getText(), is(existingGateway.getFriendlyName()));
        assertThat(webDriver.findElement(By.cssSelector(".MuiDataGrid-cell:nth-child(5)")).getText(), is(existingGateway.getDescription()));
    }

    @And("I click the first gateway in the data grid filter results and refresh with: {string}")
    public void iClickTheFirstGatewayInTheDataGridFilterResults(String refreshId) {
        Wait<WebDriver> wait = new FluentWait<>(webDriver)
                .withTimeout(Duration.ofSeconds(explicitWait))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .ignoring(ElementClickInterceptedException.class)
                .ignoring(AssertionError.class);
        wait.until((driver -> {
            driver.findElement(By.id(refreshId)).click();
            assertTrue(ExpectedConditions.textMatches(By.cssSelector(".MuiDataGrid-cell:nth-child(3)"), Pattern.compile(existingGateway.getId())).apply(driver));
            return driver.findElement((By.cssSelector(".MuiDataGrid-cell:nth-child(3)")));
        }));
        wait.until(ExpectedConditions.textMatches(By.cssSelector(".MuiDataGrid-cell:nth-child(3)"), Pattern.compile(existingGateway.getId())));
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".MuiDataGrid-row:nth-child(1) > .MuiDataGrid-cellCheckbox > .MuiButtonBase-root > .PrivateSwitchBase-input")));
        webDriver.findElement(By.cssSelector(".MuiDataGrid-row:nth-child(1) > .MuiDataGrid-cellCheckbox > .MuiButtonBase-root > .PrivateSwitchBase-input")).click();
    }
}
