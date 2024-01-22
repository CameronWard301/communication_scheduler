package io.github.cameronward301.communication_scheduler.workflows.communication_workflow.activities;

import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.exception.InvalidGatewayResponseException;
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.model.Preferences;
import io.temporal.failure.ApplicationFailure;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.codec.DecodingException;
import org.springframework.web.reactive.function.UnsupportedMediaTypeException;
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

    public SendMessageToGatewayActivityImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Map<String, String> invokeGateway(String userId, String workflowRunId, String gatewayUrl, Preferences preferences) {
        try {
            log.debug("Invoking gateway, userId: {}, workflowRunId: {}", userId, workflowRunId);
            Map<String, String> response = webClient.post()
                    .uri(gatewayUrl)
                    .bodyValue(Map.of("userId", userId, "workflowRunId", workflowRunId))
                    .retrieve()
                    .bodyToFlux(new ParameterizedTypeReference<Map<String, String>>() {
                    })
                    .timeout(Duration.ofSeconds(preferences.getGatewayTimeoutSeconds()))
                    .blockFirst();

            log.debug("Gateway send back 2xx response");
            if (response == null || response.isEmpty() || !response.containsKey("userId") || !response.containsKey("messageHash")) {
                log.error("Gateway did not return a valid response: {}", response);
                throw new InvalidGatewayResponseException();
            }

            return Map.of("status", "complete", "userId", response.get("userId"), "messageHash", response.get("messageHash"));


        } catch (WebClientResponseException e) {
            if (e.getCause() instanceof UnsupportedMediaTypeException) {
                log.error("Gateway did not return content type of application/json", e);
                throw ApplicationFailure.newFailure("Gateway did not return content type of application/json", "GatewayError");
            }
            log.error("Invalid Gateway response code", e);
            throw ApplicationFailure.newFailure("Gateway unsuccessful, status: " + e.getStatusCode().value() + " from: " + gatewayUrl, "GatewayError");
        } catch (InvalidGatewayResponseException | DecodingException e) {
            throw ApplicationFailure.newFailure("Gateway did not return a valid response", "GatewayError");
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
