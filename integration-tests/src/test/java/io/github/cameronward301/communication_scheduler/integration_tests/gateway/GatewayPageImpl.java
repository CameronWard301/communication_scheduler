package io.github.cameronward301.communication_scheduler.integration_tests.gateway;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapted from here: <a href="https://www.baeldung.com/resttemplate-page-entity-response#5-how-to-solve-httpmessageconversionexception">here</a>
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GatewayPageImpl<T> extends PageImpl<T> {
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public GatewayPageImpl(@JsonProperty("content") List<T> content, @JsonProperty("number") int number,
                           @JsonProperty("size") int size, @JsonProperty("totalElements") Long totalElements,
                           @JsonProperty("pageable") JsonNode pageable, @JsonProperty("last") boolean last,
                           @JsonProperty("totalPages") int totalPages, @JsonProperty("sort") JsonNode sort,
                           @JsonProperty("numberOfElements") int numberOfElements) {
        super(content, PageRequest.of(number, 1), 10);
    }

    public GatewayPageImpl(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public GatewayPageImpl(List<T> content) {
        super(content);
    }

    public GatewayPageImpl() {
        super(new ArrayList<>());
    }
}
