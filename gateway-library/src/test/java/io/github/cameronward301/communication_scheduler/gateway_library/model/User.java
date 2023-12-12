package io.github.cameronward301.communication_scheduler.gateway_library.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
}
