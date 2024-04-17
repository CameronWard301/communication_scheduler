package io.github.cameronward301.communication_scheduler.email_gateway.service;

import io.github.cameronward301.communication_scheduler.email_gateway.model.EmailContent;
import io.github.cameronward301.communication_scheduler.email_gateway.model.EmailUser;
import io.github.cameronward301.communication_scheduler.email_gateway.repository.ContentRepository;
import io.github.cameronward301.communication_scheduler.email_gateway.repository.UserRepository;
import io.github.cameronward301.communication_scheduler.gateway_library.exception.ResourceNotFoundException;
import io.github.cameronward301.communication_scheduler.gateway_library.service.UserContentService;
import io.github.cameronward301.communication_scheduler.gateway_library.user.content.GetUserAndContent;
import io.github.cameronward301.communication_scheduler.gateway_library.user.content.UserAndContent;
import org.springframework.stereotype.Service;

/**
 * This service is responsible for resolving the user ID to retrieve the user object and the content object to generate the message contents.
 * This service could connect to internal databases, external APIs or any other data source to retrieve the user and content objects.
 * In this example, the user and content objects are retrieved from the in-memory database for simplicity.
 * The service extends GetUserAndContent to provide the implementation for retrieving the user and content objects in separate requests,
 *   this is optional and can be omitted if it is possible for you to get both in one transaction.
 */
@Service
public class EmailUserContentService extends GetUserAndContent<EmailUser, EmailContent> implements UserContentService<EmailUser, EmailContent>  {

    private final UserRepository userRepository;
    private final ContentRepository contentRepository;

    public EmailUserContentService(UserRepository userRepository, ContentRepository contentRepository) {
        this.userRepository = userRepository;
        this.contentRepository = contentRepository;
    }

    @Override
    public UserAndContent<EmailUser, EmailContent> getUserAndContent(String userId) {
        EmailUser emailUser = getUser(userId);
        EmailContent content = getContent(userId);
        return UserAndContent.<EmailUser, EmailContent>builder().user(emailUser).content(content).build();
    }


    /**
     * Get the user details for the given userId
     * @param userId the userId of the user to get the details for
     * @return the User Object
     * @throws ResourceNotFoundException if the user is not found
     */
    @Override
    public EmailUser getUser(String userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Could not find user with id: '" + userId + "'"));
    }

    /**
     * Get the content for the given userId
     * @param userId the userId of the user to get the content for
     * @return the Content Object
     * @throws ResourceNotFoundException if the content is not found or available
     */
    @Override
    public EmailContent getContent(String userId) {
        EmailContent emailContent =  contentRepository.findByUserId(userId);
        if (emailContent == null) {
            throw new ResourceNotFoundException("Could not find content with id: '" + userId + "'");
        }
        return emailContent;
    }
}
