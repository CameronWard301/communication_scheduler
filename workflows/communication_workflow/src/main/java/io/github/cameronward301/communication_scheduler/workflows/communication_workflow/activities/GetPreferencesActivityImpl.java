package io.github.cameronward301.communication_scheduler.workflows.communication_workflow.activities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.model.Preferences;
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.properties.TemporalProperties;
import io.temporal.failure.ApplicationFailure;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Map;

/**
 * Get Preferences Activity Implementation
 */
@Slf4j
public class GetPreferencesActivityImpl implements GetPreferencesActivity {
    private final KubernetesClient client;
    private final TemporalProperties temporalProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GetPreferencesActivityImpl(TemporalProperties temporalProperties, KubernetesClient client) {
        this.temporalProperties = temporalProperties;
        this.client = client;
    }

    @Override
    public Preferences getPreferences() {
        log.debug("Retrieving config map from kubernetes client with name: {} in namespace {}", "preferences", temporalProperties.getNamespace());
        Map<String, String> preferences = client.configMaps()
                .inNamespace(temporalProperties.getNamespace())
                .withName("preferences")
                .get().getData();
        log.debug("Got config map from kubernetes cluster: {}", preferences);

        try {
            log.debug("Parsing retry policy");
            Map<String, String> retryConfigMap = objectMapper.readValue(preferences.get("RetryPolicy"), new TypeReference<>() {
            });
            log.debug("Parsed retry policy: {}", retryConfigMap);

            log.debug("Parsing gateway timeout");
            int gatewayTimeout = Integer.parseInt(preferences.get("GatewayTimeoutSeconds"));
            log.debug("Parsed gateway timeout: {}", gatewayTimeout);

            log.debug("Building preferences");
            return Preferences.builder()
                    .startToCloseTimeout(Duration.parse(retryConfigMap.get("startToCloseTimeout")))
                    .maximumAttempts(Integer.parseInt(retryConfigMap.get("maximumAttempts")))
                    .backoffCoefficient(Double.parseDouble(retryConfigMap.get("backoffCoefficient")))
                    .initialInterval(Duration.parse(retryConfigMap.get("initialInterval")))
                    .maximumInterval(Duration.parse(retryConfigMap.get("maximumInterval")))
                    .gatewayTimeoutSeconds(gatewayTimeout)
                    .build();

        } catch (JsonProcessingException e) {
            log.error("Could not convert config map to retry policy");
            throw ApplicationFailure.newFailure("Could not convert string to config map", "InvalidConfigMap", preferences.get("RetryPolicy"));
        }


    }
}
