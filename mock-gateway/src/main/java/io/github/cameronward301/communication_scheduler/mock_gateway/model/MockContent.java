package io.github.cameronward301.communication_scheduler.mock_gateway.model;

import io.github.cameronward301.communication_scheduler.gateway_library.model.Content;
import lombok.Builder;

import static java.lang.String.format;

/**
 * A POJO for a content object. This represents a businesses internal database that contains the content of a message to send.
 */
@Builder
public class MockContent implements Content {
    private static final String MOCK_MESSAGE = "HELLO_MOCK_MESSAGE";
    private String userId;
    @Override
    public String getContentString() {
        return format("%s %s", userId, MOCK_MESSAGE);
    }
}
