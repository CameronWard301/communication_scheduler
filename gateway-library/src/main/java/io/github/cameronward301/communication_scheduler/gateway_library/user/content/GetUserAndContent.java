package io.github.cameronward301.communication_scheduler.gateway_library.user.content;

/**
 * Abstract class for getting the user details and content to be sent to the user in separate methods if a database JOIN is not possible/efficient
 * Should be extended with UserContentService interface
 *
 * @param <TUser>    the type for the user object
 * @param <TContent> the type for the content object
 */
public abstract class GetUserAndContent<TUser, TContent> {
    /**
     * Gets the user details to be sent to the user from a given user Id
     *
     * @param userId the userId of the user to get the details for
     * @return the user object containing the user details
     */
    protected abstract TUser getUser(String userId);

    /**
     * Gets the content to be sent to the user from a given user Id
     *
     * @param userId the userId of the user to get the content for
     * @return the content object containing the content to be sent to the user
     */
    protected abstract TContent getContent(String userId);

}
