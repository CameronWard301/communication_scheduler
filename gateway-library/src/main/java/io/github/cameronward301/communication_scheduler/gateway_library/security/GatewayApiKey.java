package io.github.cameronward301.communication_scheduler.gateway_library.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class GatewayApiKey extends AbstractAuthenticationToken {

    private final String gatewayApiKey;

    public GatewayApiKey(String gatewayApiKey, Collection<? extends GrantedAuthority> grantedAuthorities) {
        super(grantedAuthorities);
        this.gatewayApiKey = gatewayApiKey;
        setAuthenticated(true);
    }


    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return gatewayApiKey;
    }

}
