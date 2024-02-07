package io.github.cameronward301.communication_scheduler.mock_gateway.service;

import io.github.cameronward301.communication_scheduler.gateway_library.exception.ContentDeliveryException;
import io.github.cameronward301.communication_scheduler.gateway_library.service.ContentDeliveryService;
import io.github.cameronward301.communication_scheduler.mock_gateway.model.MockContent;
import io.github.cameronward301.communication_scheduler.mock_gateway.model.MockUser;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * This service doesn't throw any exception and will always process the request for a given user ID
 * Here is where you would normally call a third party or internal API to actually send the message
 */
@Service
@Slf4j
public class MockDeliveryService implements ContentDeliveryService<MockUser, MockContent> {

    private final int waitInSeconds;

    public MockDeliveryService(@Value("${gateway.simulated-wait-seconds}") int waitInSeconds) {
        this.waitInSeconds = waitInSeconds;
    }

    /**
     * Simulates sending the content to a message api
     * @param mockUser    the user to send the content to
     * @param mockContent the content to send to the user
     * @throws ContentDeliveryException if there is a thread interruption
     */
    @SneakyThrows
    @Override
    public void sendContent(MockUser mockUser, MockContent mockContent) throws ContentDeliveryException {
        log.info("Completing request for user: {} with contentString: {}", mockUser.getId(), mockContent.getContentString());
        Thread.sleep(waitInSeconds * 1000L);
    }
}
