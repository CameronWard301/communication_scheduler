package io.github.cameronward301.communication_scheduler.sms_gateway.service;

import io.github.cameronward301.communication_scheduler.gateway_library.exception.ResourceNotFoundException;
import io.github.cameronward301.communication_scheduler.gateway_library.service.UserContentService;
import io.github.cameronward301.communication_scheduler.gateway_library.user.content.UserAndContent;
import io.github.cameronward301.communication_scheduler.sms_gateway.model.SmsUser;
import io.github.cameronward301.communication_scheduler.sms_gateway.model.UserUsage;
import io.github.cameronward301.communication_scheduler.sms_gateway.repository.UserUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
