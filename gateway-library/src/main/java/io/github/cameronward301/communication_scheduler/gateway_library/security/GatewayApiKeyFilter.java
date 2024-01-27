package io.github.cameronward301.communication_scheduler.gateway_library.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Component for adding the api key filter to the filter chain
 */
@Component
@RequiredArgsConstructor
public class GatewayApiKeyFilter extends OncePerRequestFilter {

    private final GatewayApiKeyExtractor apiKeyExtractor;

    /**
     * Filter the request and set authentication if valid
     * @param httpServletRequest sent to the gateway
     * @param httpServletResponse to be sent back to the client
     * @param filterChain filters
     * @throws ServletException if you cannot process the filter
     * @throws IOException  if you cannot process the filter
     */
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        apiKeyExtractor.getKey(httpServletRequest)
                .ifPresent(SecurityContextHolder.getContext()::setAuthentication);

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

}
