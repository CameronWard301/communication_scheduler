package io.github.cameronward301.communication_scheduler.gateway_library.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Result returned from querying the communication history database
 */
@Getter
@Setter
@Builder
public class CommunicationHistory {
    /**
     * True if the messageId is found in the database, otherwise false
     */
    private boolean previousMessageSent;

    /**
     * The hash of the previous communication message if found, otherwise null
     */
    private String previousCommunicationMessageHash;
}
