package io.github.cameronward301.communication_scheduler.workflows.communication_workflow.activities;

import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.model.Preferences;
import io.temporal.failure.ApplicationFailure;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.Map;

/**
 * Send Message To Gateway Activity Implementation
 */
@Slf4j
public class SendMessageToGatewayActivityImpl implements SendMessageToGatewayActivity {

    private final WebClient webClient;
    private final Preferences preferences;

    public SendMessageToGatewayActivityImpl(WebClient webClient, Preferences preferences) {
        this.webClient = webClient;
        this.preferences = preferences;
    }

    @Override
    public Map<String, String> invokeGateway(String userId, String workflowRunId, String gatewayUrl) {
        try {
            log.debug("Invoking gateway, userId: {}, workflowRunId: {}", userId, workflowRunId);
            Map<String, String> response = webClient.post()
                    .uri(gatewayUrl)
                    .bodyValue(Map.of("userId", userId, "workflowRunId", workflowRunId))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, String>>(){})
                    .timeout(Duration.ofSeconds(preferences.getGatewayTimeoutSeconds()))
                    .block();

            log.debug("Gateway send back 2xx response");
            if (response == null || response.isEmpty() || !response.containsKey("userId") || !response.containsKey("messageHash")) {
                log.error("Gateway did not return a valid response: {}", response);
                throw ApplicationFailure.newFailure("Gateway did not return a response", "GatewayError");
            }

            return Map.of("status", "complete", "userId", response.get("userId"), "messageHash", response.get("messageHash"));



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
    }
}
