package io.github.cameronward301.communication_scheduler.preferences_api.configuration;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KubernetesConfig {

    /**
     * Connects to the kubernetes cluster to manage its recourses
     * @return an instance of the kubernetes client
     */
    @Bean
    KubernetesClient kubernetesClient(){
        return new KubernetesClientBuilder().build();
    }
}
