package io.github.cameronward301.communication_scheduler.integration_tests.hooks;

import io.cucumber.java.After;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;

@RequiredArgsConstructor
public class WebPortalHooks {

    private final WebDriver webDriver;

    @After("@CloseBrowserAfterScenario")
    public void tearDown() {
        webDriver.quit();
    }
}
