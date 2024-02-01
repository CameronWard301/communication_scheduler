package io.github.cameronward301.communication_scheduler.integration_tests.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayTimeout {
    private Integer gatewayTimeoutSeconds;
}
