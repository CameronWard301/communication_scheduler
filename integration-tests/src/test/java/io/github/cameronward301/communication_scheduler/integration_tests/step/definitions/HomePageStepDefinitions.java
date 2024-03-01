package io.github.cameronward301.communication_scheduler.integration_tests.step.definitions;

import io.cucumber.java.en.Then;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RequiredArgsConstructor
public class HomePageStepDefinitions {
    private final WebDriver driver;

    @Then("I should see the home page")
    public void iShouldSeeTheHomePage() {
        assertThat(driver.findElement(By.id("home-title")).getText(), is("Communication Scheduling Platform"));
        assertThat(driver.findElement(By.id("home-subtitle")).getText(), is("Choose an item from the menu to get stared"));
        assertThat(driver.findElement(By.id("history-title")).getText(), is("Communication History"));
        assertThat(driver.findElement(By.id("history-subtitle")).getText(), is("View the status of previous communications sent through the platform"));
        assertThat(driver.findElement(By.id("schedules-title")).getText(), is("Communication Schedules"));
        assertThat(driver.findElement(By.id("schedules-subtitle")).getText(), is("Find and manage user's communication schedules or create new schedules"));
        assertThat(driver.findElement(By.id("gateways-title")).getText(), is("Communication Gateways"));
        assertThat(driver.findElement(By.id("gateways-subtitle")).getText(), is("View and manage the communication gateways available to the platform"));
        assertThat(driver.findElement(By.id("preferences-title")).getText(), is("Platform Configuration"));
        assertThat(driver.findElement(By.id("preferences-subtitle")).getText(), is("View and manage the platform configuration options (Advanced Users)"));
        assertThat(driver.findElement(By.id("stats-title")).getText(), is("Platform monitoring"));
        assertThat(driver.findElement(By.id("stats-subtitle")).getText(), is("View and monitor the performance of the platform and cluster"));
    }
}
