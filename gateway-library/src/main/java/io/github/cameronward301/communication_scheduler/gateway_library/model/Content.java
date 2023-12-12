package io.github.cameronward301.communication_scheduler.gateway_library.model;

/**
 * Content to be sent to the user
 * Should implement getContentString() to return the content as a string for the message hash
 */
public interface Content {
    /**
     * Gets the content to be sent to the user as a string,
     * note this is what is used to generate the message hash
     * it may not be the same as the content that is sent to the user but should be the best effort to be close to the same
     * This way changes between the content generated will be seen as a new message
     *
     * @return the content as a string
     */
    String getContentString();
}
