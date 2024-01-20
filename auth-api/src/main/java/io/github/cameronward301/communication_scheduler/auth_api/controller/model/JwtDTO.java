package io.github.cameronward301.communication_scheduler.auth_api.controller.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtDTO {
    private String token;
    private String expires;
}
