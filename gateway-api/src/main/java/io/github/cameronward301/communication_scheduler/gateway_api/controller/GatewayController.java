package io.github.cameronward301.communication_scheduler.gateway_api.controller;

import io.github.cameronward301.communication_scheduler.gateway_api.exception.RequestException;
import io.github.cameronward301.communication_scheduler.gateway_api.model.Gateway;
import io.github.cameronward301.communication_scheduler.gateway_api.service.GatewayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;


/**
 * GatewayController see API spec here: <a href="https://app.swaggerhub.com/apis/CameronWard301/Communication_APIs">Gateway API</a>
 */
@Controller
@RequestMapping("/gateway")
@RequiredArgsConstructor
public class GatewayController {
    private final GatewayService gatewayService;

    /**
     * Get all gateways with optional pagination and filtering
     *
     * @param pageNumber     the id of the last gateway returned in the previous request, can be null
     * @param pageSize     the number of gateways to return
     * @param friendlyName match results that contain this string
     * @param endpointUrl  match results that contain this string
     * @return a list of gateways matching the query
     */
    @GetMapping("")
    public ResponseEntity<Page<Gateway>> getAllGateways(
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") String pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "5") String pageSize,
            @RequestParam(value = "friendlyName", required = false, defaultValue = ".*") String friendlyName,
            @RequestParam(value = "endpointUrl", required = false, defaultValue = ".*") String endpointUrl,
            @RequestParam(value = "description", required = false, defaultValue = ".*") String description,
            @RequestParam(value = "sort", required = false, defaultValue = "dateCreated") String sortField,
            @RequestParam(value = "sortDirection", required = false, defaultValue = "desc") String sortDirection
            ) {
        return ResponseEntity.ok(gatewayService.getGateways(pageNumber, pageSize, friendlyName, endpointUrl, description, sortField, sortDirection));
    }

    /**
     * Create a new gateway
     *
     * @param gateway       the gateway to create in JSON format
     * @param bindingResult the result of the validation
     * @return the created gateway with the id and dateCreated fields populated
     * @throws RequestException if the request body is invalid
     */
    @PostMapping("")
    public ResponseEntity<Gateway> createGateway(
            @Valid @RequestBody Gateway gateway,
            BindingResult bindingResult
    ) throws RequestException {
        if (bindingResult.hasErrors()) {
            throw new RequestException(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage(), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(gatewayService.createGateway(gateway));
    }

    /**
     * Get a gateway by id
     *
     * @param id the id of the gateway to get
     * @return the gateway matching the provided id
     */
    @GetMapping("/{id}")
    public ResponseEntity<Gateway> getGatewayById(@PathVariable String id) {
        return ResponseEntity.ok(gatewayService.getGatewayById(id));
    }

    /**
     * Delete a gateway by id
     *
     * @param id the id of the gateway to delete
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteGatewayById(@PathVariable String id) {
        gatewayService.deleteGatewayById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Update a gateway
     *
     * @param gateway       the gateway to update in JSON format with the id field populated, createdDate cannot be changed
     * @param bindingResult the result of the validation
     * @return the updated gateway
     * @throws RequestException if the request body is invalid
     */
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
