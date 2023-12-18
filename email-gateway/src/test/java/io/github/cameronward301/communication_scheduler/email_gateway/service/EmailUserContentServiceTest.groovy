package io.github.cameronward301.communication_scheduler.email_gateway.service

import io.github.cameronward301.communication_scheduler.email_gateway.model.EmailContent
import io.github.cameronward301.communication_scheduler.email_gateway.model.EmailUser
import io.github.cameronward301.communication_scheduler.email_gateway.repository.ContentRepository
import io.github.cameronward301.communication_scheduler.email_gateway.repository.UserRepository
import io.github.cameronward301.communication_scheduler.gateway_library.user.content.UserAndContent
import spock.lang.Specification

class EmailUserContentServiceTest extends Specification {
    private UserRepository userRepository = Mock(UserRepository)
    private ContentRepository contentRepository = Mock(ContentRepository)

    private EmailUser emailUser = EmailUser.builder()
            .id("test-user-id")
            .email("test@example.com")
            .firstName("test")
            .build()

    private EmailContent emailContent = EmailContent.builder()
            .id("test-content-id")
            .malware(1234)
            .userId("test-user-id")
            .adverts(1111)
            .sites(2222)
            .build()

    private EmailUserContentService emailUserContentService

    def setup() {
        emailUserContentService = new EmailUserContentService(userRepository, contentRepository)
    }

    def "should return email and content from repository"() {
        given:
        userRepository.findById("test-user-id") >> Optional.of(emailUser)
        contentRepository.findByUserId("test-user-id") >> emailContent

        when:
        UserAndContent response = emailUserContentService.getUserAndContent("test-user-id")

        then:
        response.getContent().getUserId() == emailUser.getId()
        response.getContent().getMalware() == emailContent.getMalware()
        response.getContent().getAdverts() == emailContent.getAdverts()
        response.getContent().getSites() == emailContent.getSites()
        response.getContent().getId() == emailContent.getId()
        response.getContent() == emailContent

        response.getUser().getId() == emailUser.getId()
        response.getUser().getEmail() == emailUser.getEmail()
        response.getUser().getFirstName() == emailUser.getFirstName()
        response.getUser() == emailUser
    }
}
