package io.github.cameronward301.communication_scheduler.preferences_api.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GatewayTimeout {

    @NotNull(message = "'gatewayTimeoutSeconds' cannot be null")
    private Integer gatewayTimeoutSeconds;
}
