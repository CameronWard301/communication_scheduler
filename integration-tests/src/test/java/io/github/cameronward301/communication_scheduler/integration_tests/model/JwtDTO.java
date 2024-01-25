package io.github.cameronward301.communication_scheduler.integration_tests.model;

import lombok.Data;

@Data
public class JwtDTO {
    private String token;
    private String expires;
}
