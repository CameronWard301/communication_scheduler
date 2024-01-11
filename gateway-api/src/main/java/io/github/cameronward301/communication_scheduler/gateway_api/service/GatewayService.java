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

@Service
@RequiredArgsConstructor
public class GatewayService {
    private final GatewayRepository gatewayRepository;

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
            if (friendlyName != null && endpointUrl != null){
                expression = "contains(friendly_name, :friendlyName) and contains(endpoint_url, :endpointUrl)";
                expressionValues.put(":friendlyName", new AttributeValue().withS(friendlyName));
                expressionValues.put(":endpointUrl", new AttributeValue().withS(endpointUrl));
            } else if (friendlyName != null){
                expression = "contains(friendly_name, :friendlyName)";
                expressionValues.put(":friendlyName", new AttributeValue().withS(friendlyName));
            } else {
                expression = "contains(endpoint_url, :endpointUrl)";
                expressionValues.put(":endpointUrl", new AttributeValue().withS(endpointUrl));
            }
            return gatewayRepository.getGatewaysByQuery(expression, expressionValues, startKeyMap, pageSize);
        }
    }

    public Gateway createGateway(Gateway gateway) {
        gateway.setId(UUID.randomUUID().toString());
        gateway.setDateCreated(Instant.now().toString());
        gateway.setFriendlyName(gateway.getFriendlyName().toLowerCase());
        gateway.setDescription(gateway.getDescription().toLowerCase());
        gatewayRepository.save(gateway);
        return gateway;
    }

    public Gateway getGatewayById(String id) {
        PaginatedQueryList<Gateway> gateway = gatewayRepository.findById(id);
        if (gateway.isEmpty()) {
            throw new RequestException("Gateway with id '" + id + "' not found", HttpStatus.NOT_FOUND);
        }
        return gateway.getFirst();
    }

    public void deleteGatewayById(String id) {
        Gateway gateway = getGatewayById(id);
        gatewayRepository.delete(gateway);
    }

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
