package io.github.cameronward301.communication_scheduler.auth_api.controller

import io.github.cameronward301.communication_scheduler.auth_api.model.JwtDTO
import io.github.cameronward301.communication_scheduler.auth_api.service.AuthService
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.impl.DefaultClaims
import org.springframework.http.HttpStatus
import spock.lang.Specification

class AuthControllerTest extends Specification {
    private AuthController authController
    private AuthService authService = Mock(AuthService)


    def setup() {
        authController = new AuthController(authService)
    }

    def "should return 200 when (POST) get JWT is called"() {
        given: "Request parameters"
        def scopes = ["test-scope"]

        and: "AuthService returns a response"
        authService.generateJwt(scopes) >> JwtDTO.builder()
                .token(Jwts.builder()
                        .claims(Map.of("scopes", scopes))
                        .compact())
                .build()

        when:
        def response = authController.getJwt(scopes)

        then:
        response.getStatusCode() == HttpStatus.OK

        and:
        DefaultClaims claims = Jwts.parser().unsecured().build().parse(response.getBody().getToken()).getPayload() as DefaultClaims
        claims.get("scopes") == scopes
    }

    def "should return 200 when (GET) jwks is called"() {
        given: "Response map"
        Map<String, Object> jwks = Map.of("test-key", "test-value")

        and: "AuthService can't process null scope"
        authService.getJwks() >> jwks

        when:
        def response = authController.getKey()

        then:
        response.getStatusCode() == HttpStatus.OK

        and:
        response.getBody() == jwks

    }
}
