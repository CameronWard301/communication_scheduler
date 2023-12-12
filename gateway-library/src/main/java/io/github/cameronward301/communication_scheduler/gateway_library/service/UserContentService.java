package io.github.cameronward301.communication_scheduler.gateway_library.service;

import io.github.cameronward301.communication_scheduler.gateway_library.model.Content;
import io.github.cameronward301.communication_scheduler.gateway_library.user.content.UserAndContent;

/**
 * Service for getting the user details and content to be sent to the user in one method from the user id
 * Should be implemented by gateway implementation and call the getUserWithContent method in the GetUserWithContent interface
 * Alternatively the gateway implementation can call the getUserAndContent method in the GetUserAndContent interface
 *
 * @param <TUser>    the type for the user object
 * @param <TContent> the type for the content object
 */
public interface UserContentService<TUser, TContent extends Content> {

    /**
     * Gets the user and content to be sent to the user
     *
     * @param userId the userId of the user to get the content for
     * @return the user and content object containing the user details and the content to be sent to the user
     */
    UserAndContent<TUser, TContent> getUserAndContent(String userId);
}
