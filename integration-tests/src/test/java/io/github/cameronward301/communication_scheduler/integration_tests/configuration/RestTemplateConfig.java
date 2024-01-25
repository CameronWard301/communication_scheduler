package io.github.cameronward301.communication_scheduler.integration_tests.configuration;
// Adapted from: https://stackoverflow.com/questions/4072585/disabling-ssl-certificate-validation-in-spring-resttemplate to disable ssl verification

import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Configuration
public class RestTemplateConfig {

    /**
     * Create a rest template that ignores ssl verification for self-signed certificates
     */
    @Bean
    public RestTemplate getRestTemplate() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory(
                HttpClients.custom()
                        .setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
                                .setSSLSocketFactory(new SSLConnectionSocketFactory(
                                        org.apache.http.ssl.SSLContexts.custom()
                                                .loadTrustMaterial(null, (x509Certificates, s) -> true)
                                                .build(), new NoopHostnameVerifier()))
                                .build()
                        ).build());

        return new RestTemplate(httpRequestFactory);

    }
}
