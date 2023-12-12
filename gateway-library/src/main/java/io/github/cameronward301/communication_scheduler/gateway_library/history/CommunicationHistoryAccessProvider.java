package io.github.cameronward301.communication_scheduler.gateway_library.history;

import io.github.cameronward301.communication_scheduler.gateway_library.model.CommunicationHistory;

/**
 * Interface for accessing communication history data
 */
public interface CommunicationHistoryAccessProvider {
    /**
     * Gets the previous communication history for a user by message hash
     *
     * @param messageHash the hash of the message to get the previous communication history for
     * @return CommunicationHistory object containing the result
     */
    CommunicationHistory getPreviousCommunicationByMessageHash(String messageHash);

    /**
     * Removes the communication history for a user by message hash
     *
     * @param messageHash the hash of the message to remove the communication history for
     */
    void removeCommunicationHistoryByMessageHash(String messageHash);

    /**
     * Stores the communication history for a user
     *
     * @param workflowRunId the id of the workflow run that triggered the communication
     * @param userId        the id of the user whose message history is being stored
     * @param messageHash   the hash of the message being sent with the user id
     */
    void storeCommunication(String workflowRunId, String userId, String messageHash);
}
