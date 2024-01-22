package io.github.cameronward301.communication_scheduler.auth_api.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JwtDTO {
    private String token;
    private String expires;
}
