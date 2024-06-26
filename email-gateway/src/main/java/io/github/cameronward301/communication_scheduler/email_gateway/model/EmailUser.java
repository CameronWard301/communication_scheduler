package io.github.cameronward301.communication_scheduler.email_gateway.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

/**
 * Example user entity that represents a businesses customer model
 */
@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class EmailUser {
    @Id
    private String id;
    private String email;
    private String firstName;
}
