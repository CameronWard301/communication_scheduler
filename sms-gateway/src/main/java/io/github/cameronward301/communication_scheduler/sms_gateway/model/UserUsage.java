package io.github.cameronward301.communication_scheduler.sms_gateway.model;

import io.github.cameronward301.communication_scheduler.gateway_library.model.Content;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Represents the usage statistics for a user using some business product/service
 */
@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class UserUsage implements Content {
    @Id
    private String id;
    private int malwareBlocked;
    private int advertsBlocked;
    private int sitesVisited;

    @OneToOne
    private SmsUser user;

    @Override
    public String getContentString() {
        return user.getFirstName() + " " + malwareBlocked + " " + advertsBlocked + " " + sitesVisited;
    }
}
