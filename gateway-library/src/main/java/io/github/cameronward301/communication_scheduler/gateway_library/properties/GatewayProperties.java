package io.github.cameronward301.communication_scheduler.gateway_library.properties;

import io.github.cameronward301.communication_scheduler.gateway_library.history.CommunicationHistoryAccessProvider;
import io.github.cameronward301.communication_scheduler.gateway_library.model.Content;
import io.github.cameronward301.communication_scheduler.gateway_library.service.ContentDeliveryService;
import io.github.cameronward301.communication_scheduler.gateway_library.service.UserContentService;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Properties for the gateway
 *
 * @param <TUser>    the type for the user object
 * @param <TContent> the type for the content object
 */
@SuperBuilder
@Getter
@Setter
public abstract class GatewayProperties<TUser, TContent extends Content> {
    /**
     * The userId of the user that the message is being sent to
     */
    private String userId;

    /**
     * The workflowRunId of the message that is being sent
     */
    private String workflowRunId;

    /**
     * The content delivery service to use for delivering content
     */
    private ContentDeliveryService<TUser, TContent> contentDeliveryService;

    /**
     * The user content service for getting user details and he content to tbe sent
     */
    private UserContentService<TUser, TContent> userContentService;

    /**
     * The communication history access provider for querying the communication history database
     */
    private CommunicationHistoryAccessProvider communicationHistoryAccessProvider;
}
