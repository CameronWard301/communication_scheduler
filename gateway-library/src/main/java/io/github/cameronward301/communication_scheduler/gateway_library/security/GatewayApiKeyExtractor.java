package io.github.cameronward301.communication_scheduler.gateway_library.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Component responsible for reading the key from the request header
 */
@Component
@Slf4j
public class GatewayApiKeyExtractor {

    private final String gatewayApiKey;

    private static final String HEADER = "x-worker-api-key";

    public GatewayApiKeyExtractor(@Value("${io.github.cameronward301.communication-scheduler.gateway-library.worker.apiKey}") String gatewayApiKey) {
        this.gatewayApiKey = gatewayApiKey;
    }

    /**
     * For a given request find the api key and return an authentication object if it is valid
     * @param request the http request sent to the gateway
     * @return an Authentication object if valid otherwise an empty optional if invalid
     */
    public Optional<Authentication> getKey(HttpServletRequest request){
        String suppliedKey = request.getHeader(HEADER);

        if (suppliedKey == null || !suppliedKey.equals(gatewayApiKey)){
            log.debug("Invalid API Key: {}", suppliedKey);
            return Optional.empty();
        }

        return Optional.of(new GatewayApiKey(suppliedKey, AuthorityUtils.NO_AUTHORITIES));
    }
}
