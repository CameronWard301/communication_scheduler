package io.github.cameronward301.communication_scheduler.gateway_library.user.content;

import io.github.cameronward301.communication_scheduler.gateway_library.model.Content;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Stores the user and content objects for the gateway service to use
 *
 * @param <TUser>    the type for the user object
 * @param <TContent> the type for the content object
 */
@Getter
@Setter
@Builder
public class UserAndContent<TUser, TContent extends Content> {
    /**
     * The user object containing the user details
     */
    private TUser user;

    /**
     * The content object containing the content / information needed to generate the message to be sent to the user
     */
    private TContent content;
}
