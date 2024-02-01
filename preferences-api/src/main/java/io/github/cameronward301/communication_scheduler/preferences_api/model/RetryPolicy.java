package io.github.cameronward301.communication_scheduler.preferences_api.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RetryPolicy {
    @NotBlank(message = "'maximumAttempts' cannot be empty")
    private String maximumAttempts;

    @NotNull(message = "'backoffCoefficient' cannot be empty")
    private Float backoffCoefficient;

    @NotBlank(message = "'initialInterval' cannot be empty")
    private String initialInterval;

    @NotBlank(message = "'maximumInterval' cannot be empty")
    private String maximumInterval;

    @NotBlank(message = "'startToCloseTimeout' cannot be empty")
    private String startToCloseTimeout;
}
