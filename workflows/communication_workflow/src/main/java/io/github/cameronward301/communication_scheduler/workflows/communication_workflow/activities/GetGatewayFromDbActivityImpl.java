package io.github.cameronward301.communication_scheduler.workflows.communication_workflow.activities;

import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.exception.GatewayNotFoundException;
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.model.Gateway;
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.repository.GatewayRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Get Gateway From DynamoDb Implementation
 */
@Slf4j
@AllArgsConstructor
public class GetGatewayFromDbActivityImpl implements GetGatewayFromDbActivity {

    private final GatewayRepository gatewayRepository;

    @Override
    public String getGatewayEndpointUrl(String gatewayId) {
        Gateway gateway = gatewayRepository.findById(gatewayId).orElseThrow(() -> new GatewayNotFoundException(gatewayId));

        return gateway.getEndpointUrl();
    }
}
