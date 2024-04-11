package io.github.cameronward301.communication_scheduler.integration_tests.configuration;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;

@Configuration
public class WebDriverConfiguration {

    @Value("${web-portal.implicit-wait}")
    private String implicitWait;

    @Value("${web-portal.remote-web-driver-url}")
    private String remoteWebDriverUrl;

    @Bean
    public ChromeOptions chromeOptions() {
        ChromeOptions options = new ChromeOptions();
        //options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--ignore-certificate-errors");
        return options;
    }

    @Bean
    @ConditionalOnProperty(name = "web-portal.environment", havingValue = "local")
    public WebDriver webDriverLocal() {
        WebDriver driver = new ChromeDriver();
        driver.manage().window().setSize(new Dimension(1920, 1080));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(Integer.parseInt(implicitWait)));
        driver.manage().window().maximize();
        return driver;
    }

    @Bean
    @ConditionalOnProperty(name = "web-portal.environment", havingValue = "server")
    public WebDriver remoteWebDriver(ChromeOptions chromeOptions) throws MalformedURLException, URISyntaxException {

        WebDriver driver = new RemoteWebDriver(new URI(remoteWebDriverUrl).toURL(), chromeOptions);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(Integer.parseInt(implicitWait)));
        driver.manage().window().maximize();
        return driver;
    }


}
