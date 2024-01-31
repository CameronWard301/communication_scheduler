package io.github.cameronward301.communication_scheduler.integration_tests.step.definitions;


import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.cameronward301.communication_scheduler.integration_tests.model.GatewayTimeout;
import io.github.cameronward301.communication_scheduler.integration_tests.model.Preferences;
import io.github.cameronward301.communication_scheduler.integration_tests.world.World;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PreferencesAPIStepDefinitions {
    private final World world;
    private final RestTemplate restTemplate;

    private Preferences preferencesRequest;
    private GatewayTimeout gatewayTimeoutRequest;

    private ResponseEntity<Preferences> preferencesResponseEntity;
    private ResponseEntity<Preferences.RetryPolicy> retryPolicyResponseEntity;
    private ResponseEntity<GatewayTimeout> gatewayTimeoutResponseEntity;

    @Value("${preferences-api.address}")
    private String preferencesAPIUrl;

    public PreferencesAPIStepDefinitions(World world, RestTemplate restTemplate) {
        this.world = world;
        this.restTemplate = restTemplate;
    }

    @When("I get the preferences")
    public void getPreferences() {
        try {
            preferencesResponseEntity = restTemplate.exchange(preferencesAPIUrl, HttpMethod.GET, new HttpEntity<>(world.getHttpHeaders()), Preferences.class);
        } catch (HttpClientErrorException e) {
            world.setHttpClientErrorException(e);
        }
    }

    @Then("preferences are returned with a status code of {int}")
    public void preferencesAreReturnedWithAStatusCodeOf(int httpCode) {
        assertEquals(httpCode, preferencesResponseEntity.getStatusCode().value());
        assertNotNull(Objects.requireNonNull(preferencesResponseEntity.getBody()).getGatewayTimeoutSeconds());
        assertNotNull(Objects.requireNonNull(preferencesResponseEntity.getBody()).getRetryPolicy());
        assertNotNull(Objects.requireNonNull(preferencesResponseEntity.getBody()).getRetryPolicy().getMaximumAttempts());
        assertNotNull(Objects.requireNonNull(preferencesResponseEntity.getBody()).getRetryPolicy().getBackoffCoefficient());
        assertNotNull(Objects.requireNonNull(preferencesResponseEntity.getBody()).getRetryPolicy().getInitialInterval());
        assertNotNull(Objects.requireNonNull(preferencesResponseEntity.getBody()).getRetryPolicy().getMaximumInterval());
        assertNotNull(Objects.requireNonNull(preferencesResponseEntity.getBody()).getRetryPolicy().getStartToCloseTimeout());

    }

    @Given("I have a retry policy with the following data:")
    public void iHaveARetryPolicyWithTheFollowingData(DataTable retryPolicy) {
        preferencesRequest = Preferences.builder()
                .retryPolicy(Preferences.RetryPolicy.builder()
                        .maximumAttempts(retryPolicy.asMaps().get(0).get("maximumAttempts"))
                        .backoffCoefficient(Float.parseFloat(retryPolicy.asMaps().get(0).get("backoffCoefficient")))
                        .initialInterval(retryPolicy.asMaps().get(0).get("initialInterval"))
                        .maximumInterval(retryPolicy.asMaps().get(0).get("maximumInterval"))
                        .startToCloseTimeout(retryPolicy.asMaps().get(0).get("startToCloseTimeout"))
                        .build())
                .build();
    }

    @When("I update the retryPolicy")
    public void iUpdateTheRetryPolicy() {
        try {
            retryPolicyResponseEntity = restTemplate.exchange(
                    preferencesAPIUrl + "/retry-policy",
                    HttpMethod.PUT,
                    new HttpEntity<>(preferencesRequest.getRetryPolicy(), world.getHttpHeaders()),
                    Preferences.RetryPolicy.class);
        } catch (HttpClientErrorException e) {
            world.setHttpClientErrorException(e);
        }

    }

    @Then("the updated retryPolicy is returned with status code of {int}")
    public void theUpdatedRetryPolicyIsReturnedWithStatusCodeOf(int statusCode) {
        assertEquals(HttpStatusCode.valueOf(statusCode), retryPolicyResponseEntity.getStatusCode());
        assertEquals(preferencesRequest.getRetryPolicy(), retryPolicyResponseEntity.getBody());
    }

    @Given("I have a gateway timeout with value: {int}")
    public void iHaveAGatewayTimeoutWithValue(int timeoutValue) {
        gatewayTimeoutRequest = GatewayTimeout.builder()
                .gatewayTimeoutSeconds(timeoutValue)
                .build();
    }

    @And("I have a gateway timeout with value: null")
    public void iHaveAGatewayTimeoutWithValueNull() {
        gatewayTimeoutRequest = GatewayTimeout.builder()
                .build();
    }

    @When("I update the gatewayTimeout preference")
    public void iUpdateTheGatewayTimeoutPreference() {
        try {
            gatewayTimeoutResponseEntity = restTemplate.exchange(
                    preferencesAPIUrl + "/gateway-timeout",
                    HttpMethod.PUT,
                    new HttpEntity<>(gatewayTimeoutRequest, world.getHttpHeaders()),
                    GatewayTimeout.class
            );
        } catch (HttpClientErrorException e) {
            world.setHttpClientErrorException(e);
        }
    }

    @Then("the updated gatewayTimeout is returned with status code of {int}")
    public void theUpdatedGatewayTimeoutIsReturnedWithStatusCodeOf(int statusCode) {
        assertEquals(HttpStatusCode.valueOf(statusCode), gatewayTimeoutResponseEntity.getStatusCode());
        assertEquals(gatewayTimeoutRequest, gatewayTimeoutResponseEntity.getBody());
    }


}
