package io.github.cameronward301.communication_scheduler.email_gateway.model;

import io.github.cameronward301.communication_scheduler.gateway_library.model.Content;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import static java.lang.String.format;

/**
 * Represents the data that can be used to generate the email message
 */
@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class EmailContent implements Content {

    @Id
    private String id;
    private String userId;
    private int malware;
    private int adverts;
    private int sites;

    @Override
    public String getContentString() {
        return format("%s %s %s", malware, adverts, sites);
    }
}
