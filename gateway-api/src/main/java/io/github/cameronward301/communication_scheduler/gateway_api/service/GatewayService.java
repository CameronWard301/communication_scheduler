package io.github.cameronward301.communication_scheduler.gateway_api.service;

import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import io.github.cameronward301.communication_scheduler.gateway_api.exception.RequestException;
import io.github.cameronward301.communication_scheduler.gateway_api.model.Gateway;
import io.github.cameronward301.communication_scheduler.gateway_api.repository.GatewayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

/**
 * Service layer for interacting with DynamoDB
 */
@Service
@RequiredArgsConstructor
public class GatewayService {
    private final GatewayRepository gatewayRepository;

    /**
     * Gets all gateways from DynamoDB
     *
     * @param startKey     the id of the last gateway returned in the previous request, can be null
     * @param pageSize     the number of gateways to return in this page
     * @param friendlyName match results that contain this string
     * @param endpointUrl  match results that contain this string
     * @return a list of gateways in the page matching the query
     */
    public List<Gateway> getGateways(String startKey, int pageSize, String friendlyName, String endpointUrl) {
        Map<String, AttributeValue> startKeyMap = null;
        if (startKey != null && !Objects.equals(startKey, "")) {
            startKeyMap = Map.of(
                    "id", new AttributeValue(startKey)
            );
        }

        if (friendlyName == null && endpointUrl == null) {
            return gatewayRepository.getAllGateways(startKeyMap, pageSize);

        } else {
            String expression;
            Map<String, AttributeValue> expressionValues = new HashMap<>();
            if (friendlyName != null && endpointUrl != null) {
                expression = "contains(friendly_name, :friendlyName) and contains(endpoint_url, :endpointUrl)";
                expressionValues.put(":friendlyName", new AttributeValue().withS(friendlyName));
                expressionValues.put(":endpointUrl", new AttributeValue().withS(endpointUrl));
            } else if (friendlyName != null) {
                expression = "contains(friendly_name, :friendlyName)";
                expressionValues.put(":friendlyName", new AttributeValue().withS(friendlyName));
            } else {
                expression = "contains(endpoint_url, :endpointUrl)";
                expressionValues.put(":endpointUrl", new AttributeValue().withS(endpointUrl));
            }
            return gatewayRepository.getGatewaysByQuery(expression, expressionValues, startKeyMap, pageSize);
        }
    }

    /**
     * Creates a new gateway,
     * This layer is responsible for generating the id and dateCreated fields
     * The friendlyName and description fields are converted to lowercase to help with searching in future queries
     *
     * @param gateway the gateway to create
     * @return the created gateway with the id and dateCreated fields populated
     */
    public Gateway createGateway(Gateway gateway) {
        gateway.setId(UUID.randomUUID().toString());
        gateway.setDateCreated(Instant.now().toString());
        gateway.setFriendlyName(gateway.getFriendlyName().toLowerCase());
        if (gateway.getDescription() == null) {
            gateway.setDescription("");
        }
        gateway.setDescription(gateway.getDescription().toLowerCase());
        gatewayRepository.save(gateway);
        return gateway;
    }

    /**
     * Gets a gateway by id
     *
     * @param id the id of the gateway to get
     * @return the gateway matching the provided id
     */
    public Gateway getGatewayById(String id) {
        PaginatedQueryList<Gateway> gateway = gatewayRepository.findById(id);
        if (gateway.isEmpty()) {
            throw new RequestException("Gateway with id '" + id + "' not found", HttpStatus.NOT_FOUND);
        }
        return gateway.getFirst();
    }

    /**
     * Deletes a gateway by id
     *
     * @param id the id of the gateway to delete
     */
    public void deleteGatewayById(String id) {
        Gateway gateway = getGatewayById(id);
        gatewayRepository.delete(gateway);
    }

    /**
     * Updates a gateway,
     *
     * @param gateway the gateway to update with the id field populated, createdDate cannot be changed
     * @return the updated gateway
     */
    public Gateway updateGateway(Gateway gateway) {
        if (gateway.getId() == null || gateway.getId().isEmpty()) {
            throw new RequestException("Please provide a gateway 'id' in the request body", HttpStatus.BAD_REQUEST);
        }
        Gateway existingGateway = getGatewayById(gateway.getId());
        existingGateway.setFriendlyName(gateway.getFriendlyName().toLowerCase());
        existingGateway.setEndpointUrl(gateway.getEndpointUrl());
        existingGateway.setDescription(gateway.getDescription().toLowerCase());
        gatewayRepository.save(existingGateway);
        return existingGateway;
    }
}
