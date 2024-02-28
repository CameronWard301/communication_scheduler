package io.github.cameronward301.communication_scheduler.integration_tests.step.definitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import lombok.AllArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

@AllArgsConstructor
public class PreferencesPageStepDefinitions {

    private final WebDriver driver;

    @Then("I should see the preferences page")
    public void iShouldSeeThePreferencesPage() {
        assertThat(driver.findElement(By.id("preferences-page-heading")).getText(), is("Platform Configuration"));
        assertThat(driver.findElement(By.id("maximum-attempts-title")).getText(), is("Maximum Attempts"));
        assertThat(driver.findElement(By.id("gateway-timeout-title")).getText(), is("Gateway Timeout"));
        assertTrue(driver.findElement(By.id("advanced-options-button")).isDisplayed());
        assertTrue(driver.findElement(By.id("save-preferences-btn")).isDisplayed());
        assertTrue(driver.findElement(By.id("unlimited-maximum-attempts-btn")).isDisplayed());
        assertTrue(driver.findElement(By.id("max-attempts-input")).isDisplayed());
    }

    @Then("I should not see the advanced preference options")
    public void iShouldNotSeeTheAdvancedPreferenceOptions() {
        assertTrue(driver.findElements(By.id("backoff-coefficient-title")).isEmpty());
        assertTrue(driver.findElements(By.id("initial-interval-title")).isEmpty());
        assertTrue(driver.findElements(By.id("maximum-interval-title")).isEmpty());
        assertTrue(driver.findElements(By.id("start-to-close-timout-title")).isEmpty());
    }

    @Then("I should see the advanced preference options")
    public void iShouldSeeTheAdvancedOptions() {
        assertThat(driver.findElement(By.id("backoff-coefficient-title")).getText(), is("Backoff Coefficient"));
        assertTrue(driver.findElement(By.id("backoff-coefficient-input")).isDisplayed());
        assertTrue(driver.findElement(By.id("disable-backoff-coefficient-btn")).isDisplayed());

        assertThat(driver.findElement(By.id("initial-interval-title")).getText(), is("Initial Interval"));
        assertTrue(driver.findElement(By.id("initial-interval-input")).isDisplayed());

        assertThat(driver.findElement(By.id("maximum-interval-title")).getText(), is("Maximum Interval"));
        assertTrue(driver.findElement(By.id("maximum-interval-input")).isDisplayed());
        assertTrue(driver.findElement(By.id("no-limit-maximum-interval-btn")).isDisplayed());

        assertThat(driver.findElement(By.id("start-to-close-timout-title")).getText(), is("Start To Close Timeout"));
        assertTrue(driver.findElement(By.id("start-to-close-timeout-input")).isDisplayed());
        assertTrue(driver.findElement(By.id("no-limit-start-to-close-timeout-btn")).isDisplayed());

    }


    @And("preference fields are set to:")
    public void preferenceFieldsAreSetTo(DataTable fields) {
        setNumericField("max-attempts-input", fields.asMaps().get(0).get("maximumAttempts"));
        setNumericField("gateway-timeout-input", fields.asMaps().get(0).get("gatewayTimeout"));
        setNumericField("backoff-coefficient-input", fields.asMaps().get(0).get("backoffCoefficient"));
        setNumericField("initial-interval-input", fields.asMaps().get(0).get("initialInterval"));
        setNumericField("maximum-interval-input", fields.asMaps().get(0).get("maximumInterval"));
        setNumericField("start-to-close-timeout-input", fields.asMaps().get(0).get("startToCloseTimeout"));
    }

    @Then("I should see the preference confirmation modal with new values:")
    public void iShouldSeeThePreferenceConfirmationModalWithNewValues(DataTable fields) {
        assertThat(driver.findElement(By.id("transition-modal-title")).getText(), is("Save Changes"));
        assertThat(driver.findElement(By.id("max-attempts-confirm-table-cell")).getText(), is("Maximum Attempts"));
        assertThat(driver.findElement(By.id("max-attempts-new-value")).getText(), is(fields.asMaps().get(0).get("maximumAttempts")));

        assertThat(driver.findElement(By.id("gateway-timeout-confirm-table-cell")).getText(), is("Gateway Timeout"));
        assertThat(driver.findElement(By.id("gateway-timeout-new-value")).getText(), is(fields.asMaps().get(0).get("gatewayTimeout")));

        assertThat(driver.findElement(By.id("backoff-coefficient-confirm-table-cell")).getText(), is("Backoff Coefficient"));
        assertThat(driver.findElement(By.id("backoff-coefficient-new-value")).getText(), is(fields.asMaps().get(0).get("backoffCoefficient")));

        assertThat(driver.findElement(By.id("initial-interval-confirm-table-cell")).getText(), is("Initial Interval"));
        assertThat(driver.findElement(By.id("initial-interval-new-value")).getText(), is(fields.asMaps().get(0).get("initialInterval")));

        assertThat(driver.findElement(By.id("maximum-interval-confirm-table-cell")).getText(), is("Maximum Interval"));
        assertThat(driver.findElement(By.id("maximum-interval-new-value")).getText(), is(fields.asMaps().get(0).get("maximumInterval")));

        assertThat(driver.findElement(By.id("start-to-close-confirm-table-cell")).getText(), is("Start To Close Timeout"));
        assertThat(driver.findElement(By.id("start-to-close-new-value")).getText(), is(fields.asMaps().get(0).get("startToCloseTimeout")));
    }

    @Then("I set the time periods to:")
    public void iSetTheTimePeriodsTo(DataTable timePeriods) {
        setTimePeriod("gateway-timeout-time", "gateway-timeout-time", timePeriods.asMaps().get(0).get("gatewayTimeout"));
        setTimePeriod("initial-interval-time", "initial-interval-time", timePeriods.asMaps().get(0).get("initialInterval"));
        setTimePeriod("maximum-interval-time", "maximum-interval-time", timePeriods.asMaps().get(0).get("maximumInterval"));
        setTimePeriod("start-to-close-timeout-time", "start-to-close-timeout-time", timePeriods.asMaps().get(0).get("startToCloseTimeout"));
    }


    private void clearTextField(String id) {
        driver.findElement(By.id(id)).sendKeys(Keys.CONTROL + "a");
        driver.findElement(By.id(id)).sendKeys(Keys.BACK_SPACE);
    }

    private void setNumericField(String id, String value) {
        clearTextField(id);
        driver.findElement(By.id(id)).sendKeys(value);
        driver.findElement(By.id(id)).sendKeys(Keys.HOME);
        driver.findElement(By.id(id)).sendKeys(Keys.DELETE);
    }

    private void setTimePeriod(String id, String key, String timePeriod) {
        driver.findElement(By.id(id)).click();

        switch (timePeriod) {
            case "S" -> driver.findElement(By.id(key + "-seconds-item")).click();
            case "M" -> driver.findElement(By.id(key + "-minutes-item")).click();
            case "H" -> driver.findElement(By.id(key + "-hours-item")).click();
            case "D" -> driver.findElement(By.id(key + "-days-item")).click();
        }
    }

}
