package io.github.cameronward301.communication_scheduler.integration_tests.step.definitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.cameronward301.communication_scheduler.integration_tests.gateway.Gateway;
import io.github.cameronward301.communication_scheduler.integration_tests.gateway.GatewayPageImpl;
import io.github.cameronward301.communication_scheduler.integration_tests.model.JwtDTO;
import io.github.cameronward301.communication_scheduler.integration_tests.repository.GatewayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


public class GatewayAPIStepDefinitions {
    private final GatewayRepository gatewayRepository;
    private final RestTemplate restTemplate;
    private final String existingGatewayId;
    HttpClientErrorException httpClientErrorException;
    private Gateway gateway; //inject bean but also allow for new instance
    private final Map<String, String> queryParams = new HashMap<>();
    private ResponseEntity<GatewayPageImpl<Gateway>> listGatewayResponseEntity;
    private ResponseEntity<Gateway> responseEntity;
    private ResponseEntity<Void> deleteResponseEntity;
    @Value("${gateway-api.address}")
    private String gatewayAPIUrl;

    @Value("${auth-api.address}")
    private String authAPIUrl;
    HttpHeaders httpHeaders = new HttpHeaders();



    public GatewayAPIStepDefinitions(GatewayRepository gatewayRepository, Gateway gateway, RestTemplate restTemplate) {
        this.gatewayRepository = gatewayRepository;
        this.gateway = gateway;
        this.existingGatewayId = gateway.getId();
        this.restTemplate = restTemplate;
    }

    @Given("I have a gateway with the following information:")
    public void iHaveAGatewayWithTheFollowingInformation(DataTable gatewayData) {
        gateway = new Gateway();
        gateway.setId(null);
        gateway.setDateCreated(null);
        gateway.setEndpointUrl(gatewayData.asMaps().get(0).get("endpointUrl"));
        gateway.setFriendlyName(gatewayData.asMaps().get(0).get("friendlyName"));
        gateway.setDescription(gatewayData.asMaps().get(0).get("description"));
    }

    @When("I create the gateway")
    public void iCreateTheGateway() {
        try {
            responseEntity = restTemplate.exchange(gatewayAPIUrl, HttpMethod.POST, new HttpEntity<>(gateway, httpHeaders), Gateway.class);

        } catch (HttpClientErrorException e) {
            httpClientErrorException = e;
        }
    }

    @Then("a gateway is returned with a status code of {int}")
    public void theGatewayIsCreatedWithAStatusCodeOf(int statusCode) {
        assertEquals(statusCode, responseEntity.getStatusCode().value());
        assertNotEquals(null, Objects.requireNonNull(responseEntity.getBody()).getId());
        assertNotEquals(null, Objects.requireNonNull(responseEntity.getBody()).getDateCreated());
        assertEquals(gateway.getEndpointUrl(), responseEntity.getBody().getEndpointUrl());
        assertEquals(gateway.getFriendlyName().toLowerCase(), responseEntity.getBody().getFriendlyName());
        assertEquals(gateway.getDescription() != null ? gateway.getDescription().toLowerCase() : "", responseEntity.getBody().getDescription());
    }

    @And("the test framework removes the gateway")
    public void theTestFrameworkRemovesTheGateway() {
        gatewayRepository.deleteById(Objects.requireNonNull(responseEntity.getBody()).getId());
    }

    @Then("the response code is {int} and message: {string}")
    public void theResponseCodeIsAndMessage(int code, String message) {
        assertEquals(code, httpClientErrorException.getStatusCode().value());
        assertEquals(message, httpClientErrorException.getMessage());
    }

    @When("I get the gateway by id")
    public void iGetTheGatewayById() {
        try {
            responseEntity = restTemplate.exchange(gatewayAPIUrl + "/" + gateway.getId(), HttpMethod.GET, new HttpEntity<>(httpHeaders), Gateway.class);

        } catch (HttpClientErrorException e) {
            httpClientErrorException = e;
        }
    }

    @When("I get the gateway by an unknown id: {string}")
    public void iGetTheGatewayByAnUnknownId(String id) {
        try {
            responseEntity = restTemplate.exchange(gatewayAPIUrl + "/" + id, HttpMethod.GET, new HttpEntity<>(httpHeaders), Gateway.class);

        } catch (HttpClientErrorException e) {
            httpClientErrorException = e;
        }
    }

    @When("I update the existing gateway")
    public void iUpdateTheGateway() {
        try {
            gateway.setId(existingGatewayId);
            updateGateway();

        } catch (HttpClientErrorException e) {
            httpClientErrorException = e;
        }
    }

    @When("I update the gateway without an id")
    public void iUpdateTheGatewayWithoutAnId() {
        try {
            updateGateway();

        } catch (HttpClientErrorException e) {
            httpClientErrorException = e;
        }
    }

    private void updateGateway() {
        HttpEntity<Gateway> entity = new HttpEntity<>(gateway, httpHeaders);
        responseEntity = restTemplate.exchange(gatewayAPIUrl, HttpMethod.PUT, entity, Gateway.class);
    }

    @When("I delete the existing gateway by id")
    public void iDeleteTheExistingGatewayById() {
        try {
            deleteResponseEntity = restTemplate.exchange(gatewayAPIUrl + "/" + existingGatewayId, HttpMethod.DELETE, new HttpEntity<>(httpHeaders), Void.class);

        } catch (HttpClientErrorException e) {
            httpClientErrorException = e;
        }
    }

    @Then("the response code is {int}")
    public void theResponseCodeIs(int responseCode) {
        assertEquals(responseCode, deleteResponseEntity.getStatusCode().value());
    }

    @When("I delete the gateway with an unknown id: {string}")
    public void iDeleteTheGatewayWithAnUnknownId(String id) {
        try {
            deleteResponseEntity = restTemplate.exchange(gatewayAPIUrl + "/" + id, HttpMethod.DELETE, new HttpEntity<>(httpHeaders), Void.class);

        } catch (HttpClientErrorException e) {
            httpClientErrorException = e;
        }
    }

    @When("I get the list of gateways")
    public void iGetTheListOfGateways() {
        try {
            if (queryParams.isEmpty()) {
                listGatewayResponseEntity = restTemplate.exchange(gatewayAPIUrl, HttpMethod.GET, new HttpEntity<>(httpHeaders), new ParameterizedTypeReference<>() {
                });
            } else {
                UriComponentsBuilder queryUri = UriComponentsBuilder.fromHttpUrl(gatewayAPIUrl);
                for (Map.Entry<String, String> parameter : queryParams.entrySet()) {
                    queryUri.queryParam(parameter.getKey(), parameter.getValue());
                }
                listGatewayResponseEntity = restTemplate.exchange(queryUri.encode().toUriString(), HttpMethod.GET, new HttpEntity<>(httpHeaders), new ParameterizedTypeReference<>() {
                });
            }

        } catch (HttpClientErrorException e) {
            httpClientErrorException = e;
        }
    }

    @Then("the list of gateways is returned with a status code of {int}")
    public void theListOfGatewaysIsReturnedWithAStatusCodeOf(int code) {
        assertEquals(code, listGatewayResponseEntity.getStatusCode().value());
    }

    @And("there are {int} gateways in the list")
    public void thereAreGatewaysInTheList(int numberOfGateways) {
        assertEquals(numberOfGateways, Objects.requireNonNull(listGatewayResponseEntity.getBody()).getNumberOfElements());

    }

    @Given("I set the {string} query parameter to {string}")
    public void iSetTheQueryParameterTo(String parameter, String value) {
        queryParams.put(parameter, value);
    }

    @And("I have a bearer token with the {string} scope")
    public void iHaveABearerTokenWithTheScope(String scope) {
        String authToken = Objects.requireNonNull(restTemplate.postForEntity(authAPIUrl, List.of(scope), JwtDTO.class).getBody()).getToken();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add("Authorization", "Bearer " + authToken);
    }
}
