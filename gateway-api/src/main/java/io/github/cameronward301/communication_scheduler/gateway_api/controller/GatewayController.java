package io.github.cameronward301.communication_scheduler.gateway_api.controller;

import io.github.cameronward301.communication_scheduler.gateway_api.exception.RequestException;
import io.github.cameronward301.communication_scheduler.gateway_api.model.Gateway;
import io.github.cameronward301.communication_scheduler.gateway_api.service.GatewayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;


@Controller
@RequestMapping("/gateway")
@RequiredArgsConstructor
public class GatewayController {
    private final GatewayService gatewayService;

    @GetMapping("")
    public ResponseEntity<List<Gateway>> getAllGateways(
            @RequestParam(value = "startKey", required = false) String startKey,
            @RequestParam(value = "pageSize", required = false, defaultValue = "5") int pageSize,
            @RequestParam(value = "friendlyName", required = false) String friendlyName,
            @RequestParam(value = "endpointUrl", required = false) String endpointUrl
    ) {
        return ResponseEntity.ok(gatewayService.getGateways(startKey, pageSize, friendlyName, endpointUrl));
    }

    @PostMapping("")
    public ResponseEntity<Gateway> createGateway(
            @Valid @RequestBody Gateway gateway,
            BindingResult bindingResult
    ) throws RequestException {
        if (bindingResult.hasErrors()) {
            throw new RequestException(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage(), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(gatewayService.createGateway(gateway));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Gateway> getGatewayById(@PathVariable String id) {
        return ResponseEntity.ok(gatewayService.getGatewayById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteGatewayById(@PathVariable String id) {
        gatewayService.deleteGatewayById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("")
    public ResponseEntity<Gateway> updateGateway(
            @Valid @RequestBody Gateway gateway,
            BindingResult bindingResult
    ) throws RequestException {
        if (bindingResult.hasErrors()) {
            throw new RequestException(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage(), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(gatewayService.updateGateway(gateway));
    }
}
