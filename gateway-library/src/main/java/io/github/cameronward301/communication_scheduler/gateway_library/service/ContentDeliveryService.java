package io.github.cameronward301.communication_scheduler.gateway_library.service;

import io.github.cameronward301.communication_scheduler.gateway_library.exception.ContentDeliveryException;

/**
 * Service for delivering content to a user
 *
 * @param <TUser>    the type of the user object to deliver the content to
 * @param <TContent> the type of content that is being delivered
 */
public interface ContentDeliveryService<TUser, TContent> {
    /**
     * Sends the content to the user via an external API or within this method
     *
     * @param user    the user to send the content to
     * @param content the content to send to the user
     * @throws ContentDeliveryException if there is an error delivering the content. E.g. external API returns a 400 error
     */
    void sendContent(TUser user, TContent content) throws ContentDeliveryException;
}
