package io.github.cameronward301.communication_scheduler.email_gateway.service;

import io.github.cameronward301.communication_scheduler.email_gateway.model.EmailContent;
import io.github.cameronward301.communication_scheduler.email_gateway.model.EmailUser;
import io.github.cameronward301.communication_scheduler.email_gateway.repository.ContentRepository;
import io.github.cameronward301.communication_scheduler.email_gateway.repository.UserRepository;
import io.github.cameronward301.communication_scheduler.gateway_library.service.UserContentService;
import io.github.cameronward301.communication_scheduler.gateway_library.user.content.GetUserAndContent;
import io.github.cameronward301.communication_scheduler.gateway_library.user.content.UserAndContent;
import org.springframework.stereotype.Service;

@Service
public class EmailUserContentService implements UserContentService<EmailUser, EmailContent>, GetUserAndContent<EmailUser, EmailContent> {

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


    @Override
    public EmailUser getUser(String userId) {
        return userRepository.findById(userId).orElseThrow();
    }

    @Override
    public EmailContent getContent(String userId) {
        return contentRepository.findByUserId(userId);
    }
}
