package io.github.cameronward301.communication_scheduler.preferences_api.controller;

import io.github.cameronward301.communication_scheduler.preferences_api.exception.RequestException;
import io.github.cameronward301.communication_scheduler.preferences_api.model.GatewayTimeout;
import io.github.cameronward301.communication_scheduler.preferences_api.model.Preferences;
import io.github.cameronward301.communication_scheduler.preferences_api.model.RetryPolicy;
import io.github.cameronward301.communication_scheduler.preferences_api.service.PreferencesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Objects;

@Controller
@RequestMapping("/preferences")
@RequiredArgsConstructor
public class PreferencesController {

    private final PreferencesService preferencesService;
    @GetMapping("")
    @PreAuthorize("hasAuthority('SCOPE_PREFERENCES:READ')")
    public ResponseEntity<Preferences> getRetryPolicy() throws Exception {
        return ResponseEntity.ok(preferencesService.getPreferences());
    }

    @PutMapping("/retry-policy")
    @PreAuthorize("hasAuthority('SCOPE_PREFERENCES:WRITE')")
    public ResponseEntity<RetryPolicy> setRetryPolicy(@Valid @RequestBody RetryPolicy retryPolicy, BindingResult bindingResult) throws RequestException {
        if (bindingResult.hasErrors()){
            throw new RequestException(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage(), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(preferencesService.setRetryPolicy(retryPolicy));
    }

    @PutMapping("/gateway-timeout")
    @PreAuthorize("hasAuthority('SCOPE_PREFERENCES:WRITE')")
    public ResponseEntity<GatewayTimeout> setGatewayTimeout(@Valid @RequestBody GatewayTimeout gatewayTimeout, BindingResult bindingResult) throws RequestException {
        if (bindingResult.hasErrors()) {
            throw new RequestException(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage(), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(preferencesService.setGatewayTimeoutSeconds(gatewayTimeout));
    }
}
