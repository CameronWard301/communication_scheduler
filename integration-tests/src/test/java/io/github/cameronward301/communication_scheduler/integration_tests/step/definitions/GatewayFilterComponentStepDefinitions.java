package io.github.cameronward301.communication_scheduler.integration_tests.step.definitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.github.cameronward301.communication_scheduler.integration_tests.gateway.Gateway;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

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
        Wait<WebDriver> wait = new WebDriverWait(webDriver, Duration.ofSeconds(explicitWait * 10L));
        wait.until(ExpectedConditions.textMatches(By.cssSelector(".MuiDataGrid-cell:nth-child(3)"), Pattern.compile(existingGateway.getId())));
        assertThat(webDriver.findElement(By.cssSelector(".MuiDataGrid-cell:nth-child(3)")).getText(), is(existingGateway.getId()));
        assertThat(webDriver.findElement(By.cssSelector(".MuiDataGrid-cell:nth-child(4)")).getText(), is(existingGateway.getFriendlyName()));
        assertThat(webDriver.findElement(By.cssSelector(".MuiDataGrid-cell:nth-child(5)")).getText(), is(existingGateway.getDescription()));
    }

    @And("I click the first gateway in the data grid filter results")
    public void iClickTheFirstGatewayInTheDataGridFilterResults() {
        Wait<WebDriver> wait = new WebDriverWait(webDriver, Duration.ofSeconds(explicitWait * 10L));
        wait.until(ExpectedConditions.textMatches(By.cssSelector(".MuiDataGrid-cell:nth-child(3)"), Pattern.compile(existingGateway.getId())));
        webDriver.findElement(By.cssSelector(".MuiDataGrid-row:nth-child(1) > .MuiDataGrid-cellCheckbox > .MuiButtonBase-root > .PrivateSwitchBase-input")).click();
    }
}
