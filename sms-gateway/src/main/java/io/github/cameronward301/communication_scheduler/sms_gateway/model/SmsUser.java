package io.github.cameronward301.communication_scheduler.sms_gateway.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
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
