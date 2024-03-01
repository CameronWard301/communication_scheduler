package io.github.cameronward301.communication_scheduler.integration_tests.step.definitions;

import io.cucumber.java.en.Then;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RequiredArgsConstructor
public class NavBarStepDefinitions {

    private final WebDriver driver;

    @Then("The nav item with id {string} should have the text {string}")
    public void theNavItemWithIdShouldHaveTheText(String id, String text) {
        WebElement element = driver.findElement(By.cssSelector("#nav-" + id + "-label > .MuiTypography-root"));
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(driver -> element.isDisplayed());
        assertThat(element.getText(), is(text));
    }
}
