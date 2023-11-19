package com.github.cameronward301.communication_scheduler.workflows.communication_workflow;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

import java.util.Map;

@WorkflowInterface
public interface CommunicationWorkflow {

    @WorkflowMethod
    String sendCommunication(Map<String, String> payload) throws JsonProcessingException;
}
