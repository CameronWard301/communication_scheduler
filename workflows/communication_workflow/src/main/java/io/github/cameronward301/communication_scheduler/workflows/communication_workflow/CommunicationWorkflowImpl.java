package io.github.cameronward301.communication_scheduler.workflows.communication_workflow;

import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.activities.GetGatewayFromDbActivity;
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.activities.GetPreferencesActivity;
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.activities.SendMessageToGatewayActivity;
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.model.Preferences;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.common.SearchAttributeKey;
import io.temporal.common.SearchAttributeUpdate;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * Communication Workflow Implementation
 */
@Slf4j
public class CommunicationWorkflowImpl implements CommunicationWorkflow {

    @Value("${temporal-properties.task-queue}")
    private String taskQueue;
    private final GetPreferencesActivity getSettingsActivity = Workflow.newActivityStub(GetPreferencesActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(5))
                    .setRetryOptions(RetryOptions.newBuilder()
                            .setMaximumInterval(Duration.ofSeconds(5)).build())
                    .setTaskQueue(taskQueue)
                    .build());

    @Override
    public Map<String, String> sendCommunication(Map<String, String> payload) {
        log.info("Started workflow with payload: {}", payload.toString());

        String workflowId = Workflow.getInfo().getWorkflowId();
        log.info("Got workflow id {}", workflowId);
        List<String> attributes = List.of(workflowId.split(":"));
        log.info("Got attributes {}", attributes);

        Workflow.upsertTypedSearchAttributes(SearchAttributeUpdate.valueSet(SearchAttributeKey.forText("gatewayId"), attributes.get(0)));
        Workflow.upsertTypedSearchAttributes(SearchAttributeUpdate.valueSet(SearchAttributeKey.forText("userId"), attributes.get(1)));
        Workflow.upsertTypedSearchAttributes(SearchAttributeUpdate.valueSet(SearchAttributeKey.forText("scheduleId"), attributes.get(2)));

        log.info("Getting preferences");
        Preferences preferences = getSettingsActivity.getPreferences();
        log.info("Got preferences");

        log.info("Setting up activity retry policies and timeouts");
        GetGatewayFromDbActivity getGatewayFromDbActivity = Workflow.newActivityStub(GetGatewayFromDbActivity.class,
                ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(preferences.getStartToCloseTimeout())
                        .setRetryOptions(RetryOptions.newBuilder()
                                .setMaximumInterval(preferences.getMaximumInterval())
                                .setInitialInterval(preferences.getInitialInterval())
                                .setBackoffCoefficient(preferences.getBackoffCoefficient())
                                .setMaximumAttempts(preferences.getMaximumAttempts())
                                .build())
                        .build()
        );

        SendMessageToGatewayActivity sendMessageToGatewayActivity = Workflow.newActivityStub(SendMessageToGatewayActivity.class,
                ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(preferences.getStartToCloseTimeout())
                        .setRetryOptions(RetryOptions.newBuilder()
                                .setMaximumInterval(preferences.getMaximumInterval())
                                .setInitialInterval(preferences.getInitialInterval())
                                .setBackoffCoefficient(preferences.getBackoffCoefficient())
                                .setMaximumAttempts(preferences.getMaximumAttempts())
                                .build())
                        .build()
        );
        log.info("Completed set up of activity retry policies and timeouts");


        log.info("Getting gateway URL");
        String gatewayURL = getGatewayFromDbActivity.getGatewayEndpointUrl(payload.get("gatewayId"));
        log.info("Got gateway URL: {}", gatewayURL);

        log.info("Sending message to gateway");

        Map<String, String> response = sendMessageToGatewayActivity.invokeGateway(payload.get("userId"), Workflow.getInfo().getRunId(), gatewayURL, preferences);
        log.info("Got response from gateway: {}", Arrays.toString(response.entrySet().toArray()));

        return response;

    }
}
