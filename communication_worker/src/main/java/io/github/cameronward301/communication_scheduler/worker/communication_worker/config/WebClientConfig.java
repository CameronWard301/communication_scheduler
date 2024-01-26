package io.github.cameronward301.communication_scheduler.worker.communication_worker.config;

import io.grpc.netty.shaded.io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLParameters;

@Configuration
public class WebClientConfig {

    @Bean
    @ConditionalOnProperty(prefix = "web-client", name = "ssl-verification", havingValue = "true")
    WebClient secureWebclient(){
        return WebClient.create();
    }

    @Bean
    @ConditionalOnProperty(prefix = "web-client", name = "ssl-verification", havingValue = "false")
    WebClient insecureWebClient() {
        return WebClient.builder().clientConnector(new ReactorClientHttpConnector(HttpClient.create().secure(client -> {
            try {
                client.sslContext(SslContextBuilder
                        .forClient()
                        .trustManager(InsecureTrustManagerFactory.INSTANCE)
                        .build());
            } catch (SSLException e) {
                throw new RuntimeException(e);
            }
        }))).build();
    }

}
