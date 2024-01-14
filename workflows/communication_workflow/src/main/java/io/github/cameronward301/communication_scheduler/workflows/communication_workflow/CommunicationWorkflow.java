package io.github.cameronward301.communication_scheduler.workflows.communication_workflow;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

import java.util.Map;

/**
 * Communication Workflow Interface
 */
@WorkflowInterface
public interface CommunicationWorkflow {

    /**
     * Triggers a gateway to send a communication to the specified customer and returns the response
     *
     * @param payload The JSON payload for the workflow containing the userId and gatewayId stored in the scheduled workflow
     * @return JSON response from the gateway
     */
    @WorkflowMethod
    Map<String, String> sendCommunication(Map<String, String> payload);
}
