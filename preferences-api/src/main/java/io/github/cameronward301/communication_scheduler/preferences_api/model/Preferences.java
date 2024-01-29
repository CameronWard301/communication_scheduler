package io.github.cameronward301.communication_scheduler.preferences_api.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Preferences {
    private int gatewayTimeoutSeconds;
    private RetryPolicy retryPolicy;
}
