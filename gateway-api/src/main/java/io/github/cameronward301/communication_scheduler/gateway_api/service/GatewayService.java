package io.github.cameronward301.communication_scheduler.gateway_api.service;

import io.github.cameronward301.communication_scheduler.gateway_api.exception.RequestException;
import io.github.cameronward301.communication_scheduler.gateway_api.model.Gateway;
import io.github.cameronward301.communication_scheduler.gateway_api.repository.GatewayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GatewayService {
    private final GatewayRepository gatewayRepository;

    public Page<Gateway> getGateways(
            String pageNumber,
            String pageSize,
            String friendlyName,
            String endpointUrl,
            String description,
            String sortField,
            String sortDirection
    ) {
        Sort sort;
        try {
            Sort.Direction.fromString(sortDirection);
        } catch (IllegalArgumentException e) {
            throw new RequestException("Invalid sort sortDirection: '" + sortDirection + "', must be asc or desc", HttpStatus.BAD_REQUEST);
        }
        if (Sort.Direction.fromString(sortDirection).isAscending()) {
            sort = Sort.by(sortField).ascending();
        } else {
            sort = Sort.by(sortField).descending();
        }
        PageRequest pageRequest = PageRequest.of(
                pageNumber == null ? 0 : Integer.parseInt(pageNumber),
                pageSize == null ? 0 : Integer.parseInt(pageSize),
                sort
        );
        if (friendlyName == null && endpointUrl == null) {
            return gatewayRepository.findAll(pageRequest);
        } else {
            return gatewayRepository.findByFriendlyNameRegexAndEndpointUrlRegexAndDescriptionRegexIgnoreCase(
                    Objects.requireNonNull(friendlyName),
                    endpointUrl,
                    description,
                    pageRequest
            );
        }
    }

    public Gateway createGateway(Gateway gateway) {
        gateway.setId(UUID.randomUUID().toString());
        gateway.setDateCreated(Instant.now().toString());
        gateway.setFriendlyName(gateway.getFriendlyName());
        if (gateway.getDescription() == null) {
            gateway.setDescription("");
        }
        gateway.setDescription(gateway.getDescription());
        gatewayRepository.insert(gateway);
        return gateway;
    }

    public Gateway getGatewayById(String id) {
        return gatewayRepository.findById(id).orElseThrow(() -> new RequestException("Gateway with id '" + id + "' not found", HttpStatus.NOT_FOUND));
    }

    public void deleteGatewayById(String id) {
        getGatewayById(id); // check if gateway exists (throws exception if not)
        gatewayRepository.deleteById(id);
    }

    public Gateway updateGateway(Gateway gateway) {
        if (gateway.getId() == null || gateway.getId().isEmpty()) {
            throw new RequestException("Please provide a gateway 'id' in the request body", HttpStatus.BAD_REQUEST);
        }
        Gateway existingGateway = getGatewayById(gateway.getId());
        existingGateway.setFriendlyName(gateway.getFriendlyName());
        existingGateway.setEndpointUrl(gateway.getEndpointUrl());
        existingGateway.setDescription(gateway.getDescription());
        return gatewayRepository.save(existingGateway);
    }
}
