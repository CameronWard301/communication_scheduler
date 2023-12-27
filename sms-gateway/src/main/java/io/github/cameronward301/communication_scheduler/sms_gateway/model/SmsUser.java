package io.github.cameronward301.communication_scheduler.sms_gateway.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsUser {
    @Id
    private String id;
    private String firstName;
    private String phoneNumber;
}
