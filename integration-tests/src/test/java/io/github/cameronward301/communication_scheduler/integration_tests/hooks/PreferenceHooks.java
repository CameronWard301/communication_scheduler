package io.github.cameronward301.communication_scheduler.integration_tests.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.beans.factory.annotation.Value;

public class PreferenceHooks {
    private final KubernetesClient kubernetesClient;

    @Value("${kubernetes.namespace}")
    private String kubernetesNamespace;

    private ConfigMap preferences;

    public PreferenceHooks(KubernetesClient kubernetesClient) {
        this.kubernetesClient = kubernetesClient;
    }

    @Before("@GetExistingPreferences")
    public void ExistingPreferences() {
        preferences = kubernetesClient.configMaps()
                .inNamespace(kubernetesNamespace)
                .withName("preferences").get();

    }

    @After("@RevertPreferenceChanges")
    public void revertPreferences() {
        kubernetesClient.configMaps().inNamespace(kubernetesNamespace).withName("preferences").edit(configMap ->
                new ConfigMapBuilder(configMap).withData(preferences.getData()).build());
    }
}
