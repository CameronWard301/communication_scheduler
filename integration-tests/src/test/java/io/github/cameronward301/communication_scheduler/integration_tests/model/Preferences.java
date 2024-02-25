package io.github.cameronward301.communication_scheduler.integration_tests.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Preferences {
    private Integer gatewayTimeoutSeconds;
    private RetryPolicy retryPolicy;

    @Data
    @Builder
    public static class RetryPolicy {
        private Integer maximumAttempts;
        private Float backoffCoefficient;
        private String initialInterval;
        private String maximumInterval;
        private String startToCloseTimeout;
    }
}
