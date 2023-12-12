package io.github.cameronward301.communication_scheduler.gateway_library.user.content;

import io.github.cameronward301.communication_scheduler.gateway_library.model.Content;

/**
 * Interface for getting the user details and content to be sent to the user in one method from the user id if a database JOIN is possible/efficient
 * Should be implemented with UserContentService interface
 *
 * @param <TUser>    the type for the user object
 * @param <TContent> the type for the content object
 */
public interface GetUserWithContent<TUser, TContent extends Content> {
    /**
     * Gets the user details and content to be sent to the user from a given user Id
     *
     * @param userId the userId of the user to get the details and content for
     * @return The UserAndContent object containing the user details and content to be sent to the user
     */
    UserAndContent<TUser, TContent> getUserWithContent(String userId);
}
