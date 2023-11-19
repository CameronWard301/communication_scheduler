package com.github.cameronward301.communication_scheduler.workflows.communication_workflow.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

import java.util.Map;

@ActivityInterface
public interface SendMessageToGatewayActivity {

    @ActivityMethod
    Map<String, String> invokeGateway(String userId, String workflowRunId, String gatewayUrl);
}
