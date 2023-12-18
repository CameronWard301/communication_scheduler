package io.github.cameronward301.communication_scheduler.gateway_library.service

import io.github.cameronward301.communication_scheduler.gateway_library.exception.ContentDeliveryException
import io.github.cameronward301.communication_scheduler.gateway_library.helper.HashHelper
import io.github.cameronward301.communication_scheduler.gateway_library.history.CommunicationHistoryAccessProvider
import io.github.cameronward301.communication_scheduler.gateway_library.model.CommunicationHistory
import io.github.cameronward301.communication_scheduler.gateway_library.model.GatewayResponse
import io.github.cameronward301.communication_scheduler.gateway_library.model.TestContent
import io.github.cameronward301.communication_scheduler.gateway_library.model.User
import io.github.cameronward301.communication_scheduler.gateway_library.properties.GatewayProperties
import io.github.cameronward301.communication_scheduler.gateway_library.properties.GatewayPropertiesImpl
import io.github.cameronward301.communication_scheduler.gateway_library.user.content.UserAndContent
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Specification

class CommunicationGatewayServiceTest extends Specification {
    CommunicationGatewayService communicationGatewayService = new CommunicationGatewayService<User, TestContent>()
    User user = User.builder()
            .userId("test-user")
            .firstName("test-first-name")
            .lastName("test-last-name")
            .email("test-email")
            .phoneNumber("test-phone-number")
            .build()

    TestContent content = TestContent.builder()
            .contentId("test-content-id")
            .content("test-content")
            .build()

    def "sendMessage returns correct response"() {
        given: "contentDeliveryService returns correct response"
        ContentDeliveryService contentDeliveryService = Mock(ContentDeliveryService.class)

        and: "Message hash is:"
        final String hash = HashHelper.messageHash("test-workflow-run-id", content.getContentString())

        and: "CommunicationHistoryAccessProvider returns message not already sent"
        CommunicationHistoryAccessProvider communicationHistoryAccessProvider = Mock(CommunicationHistoryAccessProvider.class)
        communicationHistoryAccessProvider.getPreviousCommunicationByMessageHash(hash) >> CommunicationHistory.builder()
                .previousMessageSent(false)
                .build()

        and: "userContentService returns correct response"
        UserContentService userContentService = Mock(UserContentService.class)
        userContentService.getUserAndContent("test-user") >> UserAndContent.builder()
                .user(user)
                .content(content)
                .build()


        and: "GatewayProperties are set correctly"
        GatewayProperties gatewayProperties = GatewayPropertiesImpl.builder()
                .userId("test-user")
                .workflowRunId("test-workflow-run-id")
                .contentDeliveryService(contentDeliveryService)
                .communicationHistoryAccessProvider(communicationHistoryAccessProvider)
                .userContentService(userContentService)
                .build()

        when: "sendMessage is called"
        ResponseEntity<GatewayResponse> response = communicationGatewayService.sendCommunication(gatewayProperties)

        then: "the correct response is returned"
        response.getStatusCode() == HttpStatus.OK
        response.getBody().getUserId() == "test-user"
        response.getBody().getMessageHash() == hash
        response.getBody()
    }

    def "sendMessage returns response when message already sent"() {
        given: "contentDeliveryService returns correct response"
        ContentDeliveryService contentDeliveryService = Mock(ContentDeliveryService.class)

        and: "Message hash is:"
        final String hash = HashHelper.messageHash("test-workflow-run-id", content.getContentString())

        and: "CommunicationHistoryAccessProvider returns message already sent"
        CommunicationHistoryAccessProvider communicationHistoryAccessProvider = Mock(CommunicationHistoryAccessProvider.class)
        communicationHistoryAccessProvider.getPreviousCommunicationByMessageHash(hash) >> CommunicationHistory.builder()
                .previousMessageSent(true)
                .previousCommunicationMessageHash(HashHelper.messageHash("test-workflow-run-id", "test-content"))
                .build()

        and: "userContentService returns correct response"
        UserContentService userContentService = Mock(UserContentService.class)
        userContentService.getUserAndContent("test-user") >> UserAndContent.builder()
                .user(user)
                .content(content)
                .build()


        and: "GatewayProperties are set correctly"
        GatewayProperties gatewayProperties = GatewayPropertiesImpl.builder()
                .userId("test-user")
                .workflowRunId("test-workflow-run-id")
                .contentDeliveryService(contentDeliveryService)
                .communicationHistoryAccessProvider(communicationHistoryAccessProvider)
                .userContentService(userContentService)
                .build()

        when: "sendMessage is called"
        ResponseEntity<GatewayResponse> response = communicationGatewayService.sendCommunication(gatewayProperties)

        then: "the correct response is returned"
        response.getStatusCode() == HttpStatus.OK
        response.getBody().getUserId() == "test-user"
        response.getBody().getMessageHash() == hash
        0 * communicationHistoryAccessProvider.storeCommunication("test-user", hash)
        response.getBody()
    }

    def "sending content throws an exception returns appropriate error response"() {
        given: "userContentService returns correct response"
        UserAndContent userAndContent = UserAndContent.builder()
                .user(user)
                .content(content)
                .build()

        UserContentService userContentService = Mock(UserContentService.class)
        userContentService.getUserAndContent("test-user") >> userAndContent

        and: "contentDeliveryService throws new ContentDeliveryException"
        ContentDeliveryService contentDeliveryService = Mock(ContentDeliveryService.class)
        contentDeliveryService.sendContent(userAndContent.getUser(), userAndContent.getContent()) >> { throw new ContentDeliveryException("Could not deliver content") }

        and: "Message hash is:"
        final String hash = HashHelper.messageHash("test-workflow-run-id", content.getContentString())

        and: "CommunicationHistoryAccessProvider returns message not already sent"
        CommunicationHistoryAccessProvider communicationHistoryAccessProvider = Mock(CommunicationHistoryAccessProvider.class)
        communicationHistoryAccessProvider.getPreviousCommunicationByMessageHash(hash) >> CommunicationHistory.builder()
                .previousMessageSent(false)
                .build()

        and: "GatewayProperties are set correctly"
        GatewayProperties gatewayProperties = GatewayPropertiesImpl.builder()
                .userId("test-user")
                .workflowRunId("test-workflow-run-id")
                .contentDeliveryService(contentDeliveryService)
                .communicationHistoryAccessProvider(communicationHistoryAccessProvider)
                .userContentService(userContentService)
                .build()

        when: "sendMessage is called"
        ResponseEntity<GatewayResponse> response = communicationGatewayService.sendCommunication(gatewayProperties)

        then: "Error response is received"
        response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR
        response.getBody().getErrorMessage() == "Could not deliver content"
    }

    def "Unhandled exception produces response code 500"() {
        when: "sendMessage is called with null GatewayProperties"
        ResponseEntity<GatewayResponse> response = communicationGatewayService.sendCommunication(null)

        then: "Error response is received"
        response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR
        response.getBody().getErrorMessage() != null

    }
}
