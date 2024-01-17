package io.github.cameronward301.communication_scheduler.sms_gateway.service

import io.github.cameronward301.communication_scheduler.gateway_library.exception.ResourceNotFoundException
import io.github.cameronward301.communication_scheduler.gateway_library.user.content.UserAndContent
import io.github.cameronward301.communication_scheduler.sms_gateway.model.SmsUser
import io.github.cameronward301.communication_scheduler.sms_gateway.model.UserUsage
import io.github.cameronward301.communication_scheduler.sms_gateway.repository.UserRepository
import io.github.cameronward301.communication_scheduler.sms_gateway.repository.UserUsageRepository
import spock.lang.Specification

class SmsUserContentServiceTest extends Specification {
    private UserRepository userRepository = Mock(UserRepository)
    private UserUsageRepository userUsageRepository = Mock(UserUsageRepository)

    private SmsUser smsUser = SmsUser.builder()
            .id("test-user-id")
            .phoneNumber("1234567890")
            .firstName("test")
            .build()

    private UserUsage userUsage = UserUsage.builder()
            .id("test-usage-id")
            .user(smsUser)
            .malwareBlocked(1234)
            .advertsBlocked(1111)
            .sitesVisited(2222)
            .build()


    private SmsUserContentService smsUserContentService

    def setup() {
        smsUserContentService = new SmsUserContentService(userUsageRepository)
    }

    def "should return user and usage from repository"() {
        given:
        userUsageRepository.findByUser_Id("test-user-id") >> userUsage

        when:
        UserAndContent response = smsUserContentService.getUserAndContent("test-user-id")

        then:
        response.getContent().getId() == userUsage.getId()
        response.getContent().getUser() == userUsage.getUser()
        response.getContent().getMalwareBlocked() == userUsage.getMalwareBlocked()
        response.getContent().getAdvertsBlocked() == userUsage.getAdvertsBlocked()
        response.getContent().getSitesVisited() == userUsage.getSitesVisited()
        response.getContent() == userUsage

        response.getUser().getId() == smsUser.getId()
        response.getUser().getPhoneNumber() == smsUser.getPhoneNumber()
        response.getUser().getFirstName() == smsUser.getFirstName()
        response.getUser() == smsUser
    }

    def "Should throw exception if user could not be found"(){
        given:
        userUsageRepository.findByUser_Id("test-user-id") >> null

        when:
        UserAndContent response = smsUserContentService.getUserAndContent("test-user-id")

        then:
        def exception = thrown(ResourceNotFoundException)
        and:
        exception.getMessage() == "Could not find user and usage with id: 'test-user-id'"
    }
}
