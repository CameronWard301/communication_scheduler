package io.github.cameronward301.communication_scheduler.auth_api.configuration;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class Keys {
    @Value("${auth-api.public-key}")
    private String publicKey;

    @Value("${auth-api.private-key}")
    private String privateKey;

    @Bean
    public KeyPair keyPair() {
        return Jwts.SIG.RS512.keyPair().build();
    }

    @Bean
    public PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String publicKeyPem = publicKey.replace("-----BEGIN PUBLIC KEY----- ", "");
        publicKeyPem = publicKeyPem.replace("-----END PUBLIC KEY-----", "");
        publicKeyPem = publicKeyPem.replaceAll("\\s", "");

        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyPem);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        return keyFactory.generatePublic(encodedKeySpec);
    }

    @Bean
    public PrivateKey getPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String privateKeyPem = privateKey.replace("-----BEGIN PRIVATE KEY-----", "");
        privateKeyPem = privateKeyPem.replace("-----END PRIVATE KEY-----", "");
        privateKeyPem = privateKeyPem.replaceAll("\\s", "");

        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyPem);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec encodedKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        return keyFactory.generatePrivate(encodedKeySpec);
    }
}
