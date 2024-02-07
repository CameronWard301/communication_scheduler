package io.github.cameronward301.communication_scheduler.mock_gateway.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MockUser {
    private String id;
    private String firstName;
    private String lastName;
}
