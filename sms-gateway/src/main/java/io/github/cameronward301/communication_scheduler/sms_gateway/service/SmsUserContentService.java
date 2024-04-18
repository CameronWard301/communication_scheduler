package io.github.cameronward301.communication_scheduler.sms_gateway.service;

import io.github.cameronward301.communication_scheduler.gateway_library.exception.ResourceNotFoundException;
import io.github.cameronward301.communication_scheduler.gateway_library.service.UserContentService;
import io.github.cameronward301.communication_scheduler.gateway_library.user.content.UserAndContent;
import io.github.cameronward301.communication_scheduler.sms_gateway.model.SmsUser;
import io.github.cameronward301.communication_scheduler.sms_gateway.model.UserUsage;
import io.github.cameronward301.communication_scheduler.sms_gateway.repository.UserUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for resolving the userID to a user and their usage statistics in one transaction
 * You can extend the "GetUserAndContent" interface to provide the implementation for retrieving the user and content objects in separate requests
 * In this example, the user and content objects are retrieved from the in-memory database for simplicity but would be some external business data source in a real-world scenario
 * Utilising Spring JPA we can retrieve the user and usage statistics in one transaction.
 */
@Service
@RequiredArgsConstructor
public class SmsUserContentService implements UserContentService<SmsUser, UserUsage> {

    private final UserUsageRepository userUsageRepository;

    @Override
    public UserAndContent<SmsUser, UserUsage> getUserAndContent(String userId) {
        UserUsage userUsage = userUsageRepository.findByUser_Id(userId);
        if (userUsage == null) {
            throw new ResourceNotFoundException("Could not find user and usage with id: '" + userId + "'");
        }
        return UserAndContent.<SmsUser, UserUsage>builder().content(userUsage).user(userUsage.getUser()).build();
    }
}
