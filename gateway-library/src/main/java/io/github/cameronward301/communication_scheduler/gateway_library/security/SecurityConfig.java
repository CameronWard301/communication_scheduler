package io.github.cameronward301.communication_scheduler.gateway_library.security;

import org.springframework.beans.factory.annotation.Value;
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
@EnableWebSecurity
public class SecurityConfig {

    private final GatewayApiKeyFilter gatewayApiKeyFilter;
    private final boolean corsEnabled;
    private final boolean csrfEnabled;

    public SecurityConfig (GatewayApiKeyFilter gatewayApiKeyFilter, @Value("${security.cors.enabled}") boolean corsEnabled, @Value("${security.csrf.enabled}") boolean csrfEnabled) {
        this.gatewayApiKeyFilter = gatewayApiKeyFilter;
        this.corsEnabled = corsEnabled;
        this.csrfEnabled = csrfEnabled;
    }

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

        if (!corsEnabled) {
            httpSecurity.cors(AbstractHttpConfigurer::disable);
        }

        if (!csrfEnabled) {
            httpSecurity.csrf(AbstractHttpConfigurer::disable);
        }

        return httpSecurity.build();
    }
}
