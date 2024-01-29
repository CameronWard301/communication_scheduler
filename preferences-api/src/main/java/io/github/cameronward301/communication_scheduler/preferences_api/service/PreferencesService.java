package io.github.cameronward301.communication_scheduler.preferences_api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.github.cameronward301.communication_scheduler.preferences_api.exception.RequestException;
import io.github.cameronward301.communication_scheduler.preferences_api.model.GatewayTimeout;
import io.github.cameronward301.communication_scheduler.preferences_api.model.Preferences;
import io.github.cameronward301.communication_scheduler.preferences_api.model.RetryPolicy;
import io.github.cameronward301.communication_scheduler.preferences_api.properties.ClusterPreferences;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PreferencesService {

    private final KubernetesClient kubernetesClient;
    private final ClusterPreferences clusterPreferences;
    private final ObjectMapper objectMapper;

    /**
     * Get the preferences from the kubernetes cluster using the client
     * @return the Preferences object containing the platforms configuration
     */
    public Preferences getPreferences() {
        log.debug("Retrieving config map from kubernetes client with name: {} in namespace {}", "preferences", clusterPreferences.getNamespace());
        Map<String, String> preferences = kubernetesClient.configMaps()
                .inNamespace(clusterPreferences.getNamespace())
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
                    .retryPolicy(RetryPolicy.builder()
                            .startToCloseTimeout(retryConfigMap.get("startToCloseTimeout"))
                            .maximumAttempts(retryConfigMap.get("maximumAttempts"))
                            .backoffCoefficient(Float.parseFloat(retryConfigMap.get("backoffCoefficient")))
                            .initialInterval(retryConfigMap.get("initialInterval"))
                            .maximumInterval(retryConfigMap.get("maximumInterval"))
                            .build()
                    ).gatewayTimeoutSeconds(gatewayTimeout)
                    .build();

        } catch (JsonProcessingException e) {
            log.error("Could not convert config map to retry policy");
            throw new RequestException("Could not process preferences config map", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update the retry policy in the preferences config map to the provided retry policy. All values will be overwritten in the existing retry policy
     * @param retryPolicy to set
     * @return the updated policy
     */
    public RetryPolicy setRetryPolicy(RetryPolicy retryPolicy) {
        try {
            log.debug("Updating retry policy to: {}", retryPolicy);
            return objectMapper.readValue(kubernetesClient.configMaps().inNamespace(clusterPreferences.getNamespace()).withName("preferences").edit(
                    configMap -> {
                        try {
                            return new ConfigMapBuilder(configMap).addToData("RetryPolicy", objectMapper.writeValueAsString(retryPolicy)).build();
                        } catch (JsonProcessingException e) {
                            log.error(e.getMessage());
                            throw new RequestException("Could not save retry policy, INVALID JSON", HttpStatus.BAD_REQUEST);
                        }
                    }
            ).getData().get("RetryPolicy"), RetryPolicy.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RequestException("Could not convert policy string to object, INVALID JSON", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update the gateway timeout setting to the value provided
     * @param gatewayTimeout - the new value to set
     * @return the updated value
     */
    public GatewayTimeout setGatewayTimeoutSeconds(GatewayTimeout gatewayTimeout) {
        log.debug("Updating gateway timeout seconds to: {}", gatewayTimeout.getGatewayTimeoutSeconds());
        try {
            return objectMapper.readValue(kubernetesClient.configMaps().inNamespace(clusterPreferences.getNamespace()).withName("preferences").edit(
                    configMap -> new ConfigMapBuilder(configMap)
                            .addToData("GatewayTimeoutSeconds", String.valueOf(gatewayTimeout.getGatewayTimeoutSeconds()))
                            .build()
            ).getData().get("GatewayTimeoutSeconds"), GatewayTimeout.class);
        } catch (JsonProcessingException e) {
            throw new RequestException("Could not convert policy string to object, INVALID JSON", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
