package io.github.cameronward301.communication_scheduler.integration_tests.step.definitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.cameronward301.communication_scheduler.integration_tests.gateway.Gateway;
import io.github.cameronward301.communication_scheduler.integration_tests.gateway.GatewayPageImpl;
import io.github.cameronward301.communication_scheduler.integration_tests.repository.GatewayRepository;
import io.github.cameronward301.communication_scheduler.integration_tests.world.World;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


public class GatewayAPIStepDefinitions {
    private final GatewayRepository gatewayRepository;
    private final RestTemplate restTemplate;
    private final String existingGatewayId;
    private final World world;
    private final Map<String, String> queryParams = new HashMap<>();
    private Gateway gateway; //inject bean but also allow for new instance
    private ResponseEntity<GatewayPageImpl<Gateway>> listGatewayResponseEntity;
    private ResponseEntity<Gateway> responseEntity;
    private ResponseEntity<Void> deleteResponseEntity;
    @Value("${gateway-api.address}")
    private String gatewayAPIUrl;


    public GatewayAPIStepDefinitions(GatewayRepository gatewayRepository, Gateway gateway, RestTemplate restTemplate, World world) {
        this.gatewayRepository = gatewayRepository;
        this.gateway = gateway;
        this.existingGatewayId = gateway.getId();
        this.restTemplate = restTemplate;
        this.world = world;
    }

    @Given("I have a gateway with the following information:")
    public void iHaveAGatewayWithTheFollowingInformation(DataTable gatewayData) {
        gateway = new Gateway();
        gateway.setId(null);
        gateway.setDateCreated(null);
        gateway.setEndpointUrl(gatewayData.asMaps().get(0).get("endpointUrl"));
        gateway.setFriendlyName(gatewayData.asMaps().get(0).get("friendlyName"));
        gateway.setDescription(gatewayData.asMaps().get(0).get("description"));
        world.setGateway(gateway);
    }

    @When("I create the gateway")
    public void iCreateTheGateway() {
        try {
            responseEntity = restTemplate.exchange(gatewayAPIUrl, HttpMethod.POST, new HttpEntity<>(gateway, world.getHttpHeaders()), Gateway.class);

        } catch (HttpClientErrorException e) {
            world.setHttpClientErrorException(e);
        }
    }

    @Then("a gateway is returned with a status code of {int}")
    public void theGatewayIsCreatedWithAStatusCodeOf(int statusCode) {
        assertEquals(statusCode, responseEntity.getStatusCode().value());
        assertNotEquals(null, Objects.requireNonNull(responseEntity.getBody()).getId());
        assertNotEquals(null, Objects.requireNonNull(responseEntity.getBody()).getDateCreated());
        assertEquals(gateway.getEndpointUrl(), responseEntity.getBody().getEndpointUrl());
        assertEquals(gateway.getFriendlyName(), responseEntity.getBody().getFriendlyName());
        assertEquals(gateway.getDescription() != null ? gateway.getDescription() : "", responseEntity.getBody().getDescription());
    }

    @And("the test framework removes the gateway")
    public void theTestFrameworkRemovesTheGateway() {
        gatewayRepository.deleteById(Objects.requireNonNull(responseEntity.getBody()).getId());
    }


    @When("I get the gateway by id")
    public void iGetTheGatewayById() {
        try {
            responseEntity = restTemplate.exchange(gatewayAPIUrl + "/" + gateway.getId(), HttpMethod.GET, new HttpEntity<>(world.getHttpHeaders()), Gateway.class);

        } catch (HttpClientErrorException e) {
            world.setHttpClientErrorException(e);
        }
    }

    @When("I get the gateway by an unknown id: {string}")
    public void iGetTheGatewayByAnUnknownId(String id) {
        try {
            responseEntity = restTemplate.exchange(gatewayAPIUrl + "/" + id, HttpMethod.GET, new HttpEntity<>(world.getHttpHeaders()), Gateway.class);

        } catch (HttpClientErrorException e) {
            world.setHttpClientErrorException(e);
        }
    }

    @When("I update the existing gateway")
    public void iUpdateTheGateway() {
        try {
            gateway.setId(existingGatewayId);
            updateGateway();

        } catch (HttpClientErrorException e) {
            world.setHttpClientErrorException(e);
        }
    }

    @When("I update the gateway without an id")
    public void iUpdateTheGatewayWithoutAnId() {
        try {
            updateGateway();

        } catch (HttpClientErrorException e) {
            world.setHttpClientErrorException(e);
        }
    }

    private void updateGateway() {
        HttpEntity<Gateway> entity = new HttpEntity<>(gateway, world.getHttpHeaders());
        responseEntity = restTemplate.exchange(gatewayAPIUrl, HttpMethod.PUT, entity, Gateway.class);
    }

    @When("I delete the existing gateway by id")
    public void iDeleteTheExistingGatewayById() {
        try {
            deleteResponseEntity = restTemplate.exchange(gatewayAPIUrl + "/" + existingGatewayId, HttpMethod.DELETE, new HttpEntity<>(world.getHttpHeaders()), Void.class);

        } catch (HttpClientErrorException e) {
            world.setHttpClientErrorException(e);
        }
    }

    @Then("the response code is {int}")
    public void theResponseCodeIs(int responseCode) {
        assertEquals(responseCode, deleteResponseEntity.getStatusCode().value());
    }

    @When("I delete the gateway with an unknown id: {string}")
    public void iDeleteTheGatewayWithAnUnknownId(String id) {
        try {
            deleteResponseEntity = restTemplate.exchange(gatewayAPIUrl + "/" + id, HttpMethod.DELETE, new HttpEntity<>(world.getHttpHeaders()), Void.class);

        } catch (HttpClientErrorException e) {
            world.setHttpClientErrorException(e);
        }
    }

    @When("I get the list of gateways")
    public void iGetTheListOfGateways() {
        try {
            if (queryParams.isEmpty()) {
                listGatewayResponseEntity = restTemplate.exchange(gatewayAPIUrl, HttpMethod.GET, new HttpEntity<>(world.getHttpHeaders()), new ParameterizedTypeReference<>() {
                });
            } else {
                UriComponentsBuilder queryUri = UriComponentsBuilder.fromHttpUrl(gatewayAPIUrl);
                for (Map.Entry<String, String> parameter : queryParams.entrySet()) {
                    queryUri.queryParam(parameter.getKey(), parameter.getValue());
                }
                listGatewayResponseEntity = restTemplate.exchange(queryUri.encode().toUriString(), HttpMethod.GET, new HttpEntity<>(world.getHttpHeaders()), new ParameterizedTypeReference<>() {
                });
            }

        } catch (HttpClientErrorException e) {
            world.setHttpClientErrorException(e);
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
}
