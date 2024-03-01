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

/**
 * Controller for managing the preferences config map
 */
@Controller
@RequestMapping("/preferences")
@RequiredArgsConstructor
public class PreferencesController {

    private final PreferencesService preferencesService;

    /**
     * Get the preferences from the cluster
     *
     * @return Preferences object containing the platforms configuration
     */
    @GetMapping("")
    @PreAuthorize("hasAuthority('SCOPE_PREFERENCES:READ')")
    public ResponseEntity<Preferences> getPreferences() {
        return ResponseEntity.ok(preferencesService.getPreferences());
    }

    /**
     * Update the platforms retry policy
     *
     * @param retryPolicy   - the retry policy to set
     * @param bindingResult - validation errors from the provided retry policy request
     * @return the updated retry-policy
     * @throws RequestException if there is an error processing the update
     */
    @PutMapping("/retry-policy")
    @PreAuthorize("hasAuthority('SCOPE_PREFERENCES:WRITE')")
    public ResponseEntity<RetryPolicy> setRetryPolicy(@Valid @RequestBody RetryPolicy retryPolicy, BindingResult bindingResult) throws RequestException {
        if (bindingResult.hasErrors()) {
            throw new RequestException(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage(), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(preferencesService.setRetryPolicy(retryPolicy));
    }

    /**
     * Update the platforms gateway timeout value
     *
     * @param gatewayTimeout the new value to set
     * @param bindingResult  validation errors of the new value
     * @return the updated setting
     * @throws RequestException if there is an error processing the update
     */
    @PutMapping("/gateway-timeout")
    @PreAuthorize("hasAuthority('SCOPE_PREFERENCES:WRITE')")
    public ResponseEntity<GatewayTimeout> setGatewayTimeout(@Valid @RequestBody GatewayTimeout gatewayTimeout, BindingResult bindingResult) throws RequestException {
        if (bindingResult.hasErrors()) {
            throw new RequestException(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage(), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(preferencesService.setGatewayTimeoutSeconds(gatewayTimeout));
    }
}
