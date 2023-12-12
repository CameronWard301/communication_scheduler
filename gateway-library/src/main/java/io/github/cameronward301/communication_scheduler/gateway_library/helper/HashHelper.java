package io.github.cameronward301.communication_scheduler.gateway_library.helper;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Helper class providing a method to hash a message
 */
public class HashHelper {
    /**
     * Hashes a message using SHA3-256 from the contents of the message and the workflow run id
     *
     * @param workflowRunId   the id of the workflow run that triggered the communication
     * @param messageContents the contents of the message
     * @return the hash of the message
     */
    public static String messageHash(String workflowRunId, String messageContents) {
        return new DigestUtils("SHA3-256").digestAsHex(workflowRunId + messageContents);
    }
}
