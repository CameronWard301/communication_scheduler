package com.github.cameronward301.communication_scheduler.workflows.communication_workflow.activities;

import io.temporal.failure.ApplicationFailure;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.json.JsonParser;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
public class SendMessageToGatewayActivityImpl implements SendMessageToGatewayActivity {

    private final WebClient webClient;

    public SendMessageToGatewayActivityImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Map<String, String> invokeGateway(String userId, String workflowRunId, String gatewayUrl) {
        webClient.post()
                .uri(gatewayUrl)
                .bodyValue(Map.of("userId", userId, "workflowRunId", workflowRunId))
                .retrieve()
                .toEntity(String.class)

                .subscribe(
                        responseEntity -> {
                            if (responseEntity.getStatusCode() != HttpStatusCode.valueOf(200)) {
                                throw ApplicationFailure.newFailure("Gateway unsuccessful: " + responseEntity.getStatusCode(), "GatewayError");
                            }
                        },
                        error -> {
                            throw ApplicationFailure.newFailure("Could not invoke gateway", "GatewayError");
                        }
                );

        return Map.of("status", "complete", "userId", userId, "gateway_url", gatewayUrl);
    }
}
