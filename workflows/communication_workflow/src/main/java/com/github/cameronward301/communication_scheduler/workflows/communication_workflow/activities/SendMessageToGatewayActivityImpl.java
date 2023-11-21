package com.github.cameronward301.communication_scheduler.workflows.communication_workflow.activities;

import com.github.cameronward301.communication_scheduler.workflows.communication_workflow.properties.ActivityProperties;
import io.temporal.failure.ApplicationFailure;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

/**
 * Send Message To Gateway Activity Implementation
 */
@Slf4j
public class SendMessageToGatewayActivityImpl implements SendMessageToGatewayActivity {

    private final WebClient webClient;
    private final ActivityProperties activityProperties;

    public SendMessageToGatewayActivityImpl(WebClient webClient, ActivityProperties activityProperties) {
        this.webClient = webClient;
        this.activityProperties = activityProperties;
    }

    @Override
    public Map<String, String> invokeGateway(String userId, String workflowRunId, String gatewayUrl) {
        try {
            log.debug("Invoking gateway, userId: {}, workflowRunId: {}", userId, workflowRunId);
            webClient.post()
                    .uri(gatewayUrl)
                    .bodyValue(Map.of("userId", userId, "workflowRunId", workflowRunId))
                    .retrieve()
                    .toEntity(String.class)
                    .timeout(java.time.Duration.ofSeconds(activityProperties.getGateway_timeout_seconds()))
                    .block();
            log.debug("Gateway send back 2xx response");

        } catch (WebClientResponseException e) {
            log.error("Invalid Gateway response", e);
            throw ApplicationFailure.newFailure("Gateway unsuccessful, status: " + e.getStatusCode().value() + " from: " + gatewayUrl, "GatewayError");
        } catch (Exception e) {
            if (e.getCause() instanceof java.util.concurrent.TimeoutException) {
                log.error("Gateway timed out", e);
                throw ApplicationFailure.newFailure("Gateway timed out: " + gatewayUrl, "GatewayError");
            }
            log.error("Could not invoke gateway", e);
            throw ApplicationFailure.newFailure("Could not invoke gateway: " + gatewayUrl, "GatewayError");
        }


        return Map.of("status", "complete", "userId", userId, "gateway_url", gatewayUrl);
    }
}
