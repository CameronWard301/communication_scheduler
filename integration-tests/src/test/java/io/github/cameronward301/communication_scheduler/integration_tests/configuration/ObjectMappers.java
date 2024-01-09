package io.github.cameronward301.communication_scheduler.integration_tests.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class ObjectMappers {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public ObjectReader objectReader(ObjectMapper objectMapper) {
        return objectMapper.readerFor(Map.class);
    }
}
