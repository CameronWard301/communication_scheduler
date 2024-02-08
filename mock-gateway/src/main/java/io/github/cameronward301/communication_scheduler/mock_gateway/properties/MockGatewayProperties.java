package io.github.cameronward301.communication_scheduler.mock_gateway.properties;

import io.github.cameronward301.communication_scheduler.gateway_library.properties.GatewayProperties;
import io.github.cameronward301.communication_scheduler.mock_gateway.model.MockContent;
import io.github.cameronward301.communication_scheduler.mock_gateway.model.MockUser;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class MockGatewayProperties extends GatewayProperties<MockUser, MockContent> {

}
