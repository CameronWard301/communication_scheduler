package com.github.cameronward301.communication_scheduler.workflows.communication_workflow.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * Activity interface for getting the gateway endpoint url from the database
 */
@ActivityInterface
public interface GetGatewayFromDbActivity {

    /**
     * Gets the gateway endpoint url from the database
     * @param gatewayId The id of the gateway to retrieve from DynamoDB
     * @return The gateway endpoint url e.g. <a href="https://example.com/monthly-email">https://example.com/monthly-email</a>
     */
    @ActivityMethod
    String getGatewayEndpointUrl(String gatewayId);
}
