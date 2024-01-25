package io.github.cameronward301.communication_scheduler.auth_api.controller;

import io.github.cameronward301.communication_scheduler.auth_api.model.JwtDTO;
import io.github.cameronward301.communication_scheduler.auth_api.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("")
    public ResponseEntity<JwtDTO> getJwt(@RequestBody List<String> scopes) {

        return ResponseEntity.ok(authService.generateJwt(scopes));
    }

    @GetMapping("/.well-known/jwks.json")
    public ResponseEntity<Map<String, Object>> getKey() {
        return ResponseEntity.ok(authService.getJwks());

    }
}
