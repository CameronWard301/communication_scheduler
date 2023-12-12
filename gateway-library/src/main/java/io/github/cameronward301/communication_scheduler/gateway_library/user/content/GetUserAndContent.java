package io.github.cameronward301.communication_scheduler.gateway_library.user.content;

/**
 * Interface for getting the user details and content to be sent to the user in separate methods if a database JOIN is not possible/efficient
 * Should be implemented with UserContentService interface
 *
 * @param <TUser>    the type for the user object
 * @param <TContent> the type for the content object
 */
public interface GetUserAndContent<TUser, TContent> {
    /**
     * Gets the user details to be sent to the user from a given user Id
     *
     * @param userId the userId of the user to get the details for
     * @return the user object containing the user details
     */
    TUser getUser(String userId);

    /**
     * Gets the content to be sent to the user from a given user Id
     *
     * @param userId the userId of the user to get the content for
     * @return the content object containing the content to be sent to the user
     */
    TContent getContent(String userId);

}
