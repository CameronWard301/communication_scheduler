package io.github.cameronward301.communication_scheduler.gateway_library.security;

import io.github.cameronward301.communication_scheduler.gateway_library.configuration.SecurityConfigurationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Secures all gateway endpoints behind a valid api key to be provided by the worker
 */
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final GatewayApiKeyFilter gatewayApiKeyFilter;
    private final SecurityConfigurationProperties securityConfigurationProperties;

    /**
     * Bean for securing the application
     * @param httpSecurity spring security configuration
     * @return configured filter chain
     * @throws Exception if filter cannot filter the request
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .addFilterBefore(gatewayApiKeyFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated())
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        if (!securityConfigurationProperties.getCors().isEnabled()) {
            httpSecurity.cors(AbstractHttpConfigurer::disable);
        }

        if (!securityConfigurationProperties.getCsrf().isEnabled()) {
            httpSecurity.csrf(AbstractHttpConfigurer::disable);
        }

        return httpSecurity.build();
    }
}
