package io.github.cameronward301.communication_scheduler.gateway_library.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@ConfigurationProperties(prefix = "io.github.cameronward301.communication-scheduler.gateway-library.security")
@Configuration
@Getter
@Setter
public class SecurityConfigurationProperties {

    private Cors cors = new Cors(true);
    private Csrf csrf = new Csrf(true);

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Cors {
        private boolean enabled;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Csrf {
        private boolean enabled;
    }
}
