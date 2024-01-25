package io.github.cameronward301.communication_scheduler.auth_api.service

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.KeyUse
import com.nimbusds.jose.jwk.RSAKey
import io.github.cameronward301.communication_scheduler.auth_api.model.JwtDTO
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.impl.DefaultClaims
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import spock.lang.Specification

import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPublicKey
import java.util.concurrent.TimeUnit

@SpringBootTest()
@ExtendWith(SpringExtension.class)
@ActiveProfiles("ssl")
class AuthServiceTest extends Specification {

    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA")
    def keyId = "communication-auth-api"
    def expiry = 3600

    AuthService authService
    KeyPair pair

    def setup() {
        generator.initialize(2048)
        pair = generator.generateKeyPair()

        authService = new AuthService(pair.getPrivate(),
                new JWKSet(
                        new RSAKey.Builder((RSAPublicKey) pair.getPublic())
                                .keyUse(KeyUse.SIGNATURE)
                                .algorithm(JWSAlgorithm.RS512)
                                .keyID(keyId).build()),
                expiry,
                keyId)
    }

    def "Should generate JWT token"() {
        given:
        List<String> scopes = ["test-scope"]

        when:
        JwtDTO jwtDTO = authService.generateJwt(scopes)

        then:
        jwtDTO.getToken() != null

        and:
        jwtDTO.getExpires() != null

        and:
        def jwt = Jwts.parser().verifyWith(pair.getPublic()).build().parse(jwtDTO.getToken())
        DefaultClaims claims = jwt.getPayload() as DefaultClaims
        claims.get("scopes") == scopes
        jwt.getHeader().get("kid") == keyId
        jwt.getHeader().get("alg") == "RS256"
        jwt.getPayload()["iss"] == "communication-auth-api"
        jwt.getPayload()["exp"] != null
        jwt.getPayload()["iat"] != null
        jwt.getPayload()["jti"] != null
        new Date(TimeUnit.SECONDS.toMillis(jwt.getPayload()["exp"] as long)).toString() == jwtDTO.getExpires()
    }

    def "Should return JWKs"() {
        when:
        def jwks = authService.getJwks()

        then:
        jwks.size() == 1
        (jwks.get("keys") as List).size() == 1

        and:
        def key = (jwks.get("keys") as List).get(0) as Map
        key.get("kty") == "RSA"
        key.get("use") == "sig"
        key.get("kid") == keyId
        key.get("alg") == "RS512"
        key.get("n") != null
        key.get("e") != null

    }
}
