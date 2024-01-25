package io.github.cameronward301.communication_scheduler.auth_api.service;

import com.nimbusds.jose.jwk.JWKSet;
import io.github.cameronward301.communication_scheduler.auth_api.model.JwtDTO;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    private final String keyId;
    private final long expiration_duration_seconds;
    private final PrivateKey key;
    private final JWKSet jwkSet;

    public AuthService(PrivateKey key, JWKSet jwkSet,
                       @Value("${auth-api.token-expiration-seconds}")
                       long expiration_duration_seconds,

                       @Value("${auth-api.key-id}")
                       String keyId
    ) {
        this.key = key;
        this.jwkSet = jwkSet;
        this.expiration_duration_seconds = expiration_duration_seconds;
        this.keyId = keyId;
    }

    public JwtDTO generateJwt(List<String> scopes) {

        Date expiration = new Date(System.currentTimeMillis() + (expiration_duration_seconds * 1000));

        String jwt = Jwts.builder()
                .issuer("communication-auth-api")
                .expiration(expiration)
                .issuedAt(new Date())
                .id(UUID.randomUUID().toString())
                .header()
                .keyId(keyId)
                .and()
                .claim("scopes", scopes)
                .signWith(key)
                .compact();

        return JwtDTO.builder()
                .token(jwt)
                .expires(expiration.toString())
                .build();
    }

    public Map<String, Object> getJwks() {
        return jwkSet.toJSONObject();
    }
}
