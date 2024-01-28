package io.github.cameronward301.communication_scheduler.preferences_api.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Preferences {
    private int gatewayTimeoutSeconds;
    private RetryPolicy retryPolicy;
}
