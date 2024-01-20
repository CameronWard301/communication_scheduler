package io.github.cameronward301.communication_scheduler.auth_api.service;

import com.nimbusds.jose.jwk.JWKSet;
import io.github.cameronward301.communication_scheduler.auth_api.controller.model.JwtDTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.JwkSet;
import io.jsonwebtoken.security.Jwks;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecretJwk;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${auth-api.key-id}")
    private String keyId;

    @Value("${auth-api.token-expiration-seconds}")
    private long expiration_duration_seconds;

    private final PrivateKey key;
    private final JWKSet jwkSet;

    public JwtDTO generateJwt(List<String> scopes) {

        Date expiration = new Date(System.currentTimeMillis() + ( expiration_duration_seconds * 1000 ));

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
