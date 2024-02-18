package io.github.cameronward301.communication_scheduler.schedule_api.configuration;

import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Configuration
public class RestTemplateConfig {

    @Value("${schedule.verify-hostnames}")
    private boolean verifyHostnames;

    /*
     * Create a rest template that ignores ssl verification for self-signed certificates if set in application.yaml
     */
    @Bean
    public RestTemplate getRestTemplate() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        if (verifyHostnames) {
            return new RestTemplate();
        }

        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory(
                HttpClients.custom()
                        .setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
                                .setSSLSocketFactory(new SSLConnectionSocketFactory(
                                        SSLContexts.custom()
                                                .loadTrustMaterial(null, (x509Certificates, s) -> true)
                                                .build(), new NoopHostnameVerifier()))
                                .build()
                        ).build());

        return new RestTemplate(httpRequestFactory);

    }
}
