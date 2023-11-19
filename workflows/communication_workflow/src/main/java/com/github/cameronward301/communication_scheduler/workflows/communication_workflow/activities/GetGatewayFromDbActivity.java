package com.github.cameronward301.communication_scheduler.workflows.communication_workflow.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface GetGatewayFromDbActivity {

    @ActivityMethod
    String getGatewayEndpointUrl(String gatewayId);
}
