package io.github.cameronward301.communication_scheduler.integration_tests.step.definitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.github.cameronward301.communication_scheduler.integration_tests.model.JwtDTO;
import io.github.cameronward301.communication_scheduler.integration_tests.world.World;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SharedStepDefinitions {

    private final World world;
    private final RestTemplate restTemplate;

    @Value("${auth-api.address}")
    private String authAPIUrl;

    public SharedStepDefinitions(World world, RestTemplate restTemplate) {
        this.world = world;
        this.restTemplate = restTemplate;
    }

    @And("I have a bearer token with the {string} scope")
    public void iHaveABearerTokenWithTheScope(String scope) {
        HttpHeaders httpHeaders = world.getHttpHeaders();
        String authToken = Objects.requireNonNull(restTemplate.postForEntity(authAPIUrl, List.of(scope), JwtDTO.class).getBody()).getToken();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("Authorization", "Bearer " + authToken);
        world.setHttpHeaders(httpHeaders);
    }

    @And("I have no token")
    public void iHaveNoToken() {
        HttpHeaders httpHeaders = world.getHttpHeaders();
        if (httpHeaders.get("Authorization") != null) {
            httpHeaders.remove("Authorization");
        }
    }

    @Then("the response code is {int} and message: {string}")
    public void theResponseCodeIsAndMessage(int code, String message) {
        assertEquals(code, world.getHttpClientErrorException().getStatusCode().value());
        assertEquals(message, world.getHttpClientErrorException().getMessage());
    }
}
