package io.github.cameronward301.communication_scheduler.integration_tests.step.definitions;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.sl.In;
import io.github.cameronward301.communication_scheduler.integration_tests.gateway.GatewayDbModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


public class GatewayAPIStepDefinitions {
    private final DynamoDBMapper dynamoDBMapper;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String existingGatewayId;

    private GatewayDbModel gatewayDbModel; //inject bean but also allow for new instance

    private Map<String, String> queryParams = new HashMap<>();
    private List<GatewayDbModel> existingGateways;
    private ResponseEntity<GatewayDbModel[]> listGatewayResponseEntity;
    private ResponseEntity<GatewayDbModel> responseEntity;
    private ResponseEntity<Void> deleteResponseEntity;

    HttpClientErrorException httpClientErrorException;

    @Value("${gateway-api.address}")
    private String gatewayAPIUrl;

    public GatewayAPIStepDefinitions(DynamoDBMapper dynamoDBMapper, GatewayDbModel gatewayDbModel) {
        this.dynamoDBMapper = dynamoDBMapper;
        this.gatewayDbModel = gatewayDbModel;
        this.existingGatewayId = gatewayDbModel.getId();
    }

    @Given("I have a gateway with the following information:")
    public void iHaveAGatewayWithTheFollowingInformation(DataTable gatewayData) {
        gatewayDbModel = new GatewayDbModel();
        gatewayDbModel.setId(null);
        gatewayDbModel.setDateCreated(null);
        gatewayDbModel.setEndpointUrl(gatewayData.asMaps().get(0).get("endpointUrl"));
        gatewayDbModel.setFriendlyName(gatewayData.asMaps().get(0).get("friendlyName"));
        gatewayDbModel.setDescription(gatewayData.asMaps().get(0).get("description"));
    }

    @When("I create the gateway")
    public void iCreateTheGateway() {
        try {
            responseEntity = restTemplate.postForEntity(gatewayAPIUrl, gatewayDbModel, GatewayDbModel.class);

        } catch (HttpClientErrorException e) {
            httpClientErrorException = e;
        }
    }

    @Then("a gateway is returned with a status code of {int}")
    public void theGatewayIsCreatedWithAStatusCodeOf(int statusCode) {
        assertEquals(statusCode, responseEntity.getStatusCode().value());
        assertNotEquals(null, Objects.requireNonNull(responseEntity.getBody()).getId());
        assertNotEquals(null, Objects.requireNonNull(responseEntity.getBody()).getDateCreated());
        assertEquals(gatewayDbModel.getEndpointUrl(), responseEntity.getBody().getEndpointUrl());
        assertEquals(gatewayDbModel.getFriendlyName().toLowerCase(), responseEntity.getBody().getFriendlyName());
        assertEquals(gatewayDbModel.getDescription() != null ? gatewayDbModel.getDescription().toLowerCase() : "", responseEntity.getBody().getDescription());
    }

    @And("the test framework removes the gateway")
    public void theTestFrameworkRemovesTheGateway() {
        dynamoDBMapper.delete(responseEntity.getBody());
    }

    @Then("the response code is {int} and message: {string}")
    public void theResponseCodeIsAndMessage(int code, String message) {
        assertEquals(code, httpClientErrorException.getStatusCode().value());
        assertEquals(message, httpClientErrorException.getMessage());
    }

    @When("I get the gateway by id")
    public void iGetTheGatewayById() {
        try {
            responseEntity = restTemplate.getForEntity(gatewayAPIUrl + "/" + gatewayDbModel.getId(), GatewayDbModel.class);

        } catch (HttpClientErrorException e) {
            httpClientErrorException = e;
        }
    }

    @When("I get the gateway by an unknown id: {string}")
    public void iGetTheGatewayByAnUnknownId(String id) {
        try {
            responseEntity = restTemplate.getForEntity(gatewayAPIUrl + "/" + id, GatewayDbModel.class);

        } catch (HttpClientErrorException e) {
            httpClientErrorException = e;
        }
    }

    @When("I update the existing gateway")
    public void iUpdateTheGateway() {
        try {
            gatewayDbModel.setId(existingGatewayId);
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

    private void updateGateway(){
        HttpEntity<GatewayDbModel> entity = new HttpEntity<>(gatewayDbModel);
        responseEntity = restTemplate.exchange(gatewayAPIUrl, HttpMethod.PUT, entity, GatewayDbModel.class);
    }

    @When("I delete the existing gateway by id")
    public void iDeleteTheExistingGatewayById() {
        try {
            deleteResponseEntity = restTemplate.exchange(gatewayAPIUrl + "/" + existingGatewayId, HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);

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
            deleteResponseEntity = restTemplate.exchange(gatewayAPIUrl + "/" + id, HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);

        } catch (HttpClientErrorException e) {
            httpClientErrorException = e;
        }
    }

    @Given("I already have the following gateways:")
    public void iAlreadyHaveTheFollowingGateways(DataTable gateways) {
        for (int i = 0; i < gateways.asMaps().size(); i++) {
            GatewayDbModel gatewayDbModel = new GatewayDbModel();
            gatewayDbModel.setId(UUID.randomUUID().toString());
            gatewayDbModel.setDateCreated(Instant.now().toString());
            gatewayDbModel.setEndpointUrl(gateways.asMaps().get(i).get("endpointUrl"));
            gatewayDbModel.setFriendlyName(gateways.asMaps().get(i).get("friendlyName"));
            gatewayDbModel.setDescription(gateways.asMaps().get(i).get("description"));
            dynamoDBMapper.save(gatewayDbModel);
            existingGateways.add(gatewayDbModel);
        }
    }

    @When("I get the list of gateways")
    public void iGetTheListOfGateways() {
        try {
            if (queryParams.isEmpty()) {
                listGatewayResponseEntity = restTemplate.getForEntity(gatewayAPIUrl, GatewayDbModel[].class);
            } else {
                UriComponentsBuilder queryUri = UriComponentsBuilder.fromHttpUrl(gatewayAPIUrl);
                for (Map.Entry<String, String> parameter : queryParams.entrySet()) {
                    queryUri.queryParam(parameter.getKey(), parameter.getValue());
                }
                listGatewayResponseEntity = restTemplate.getForEntity(queryUri.encode().toUriString(), GatewayDbModel[].class);
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
        assertEquals(numberOfGateways, Objects.requireNonNull(listGatewayResponseEntity.getBody()).length);

    }

    @Given("I set the {string} query parameter to {string}")
    public void iSetTheQueryParameterTo(String parameter, String value) {
        queryParams.put(parameter, value);
    }
}
