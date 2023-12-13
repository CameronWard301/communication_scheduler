package io.github.cameronward301.communication_scheduler.gateway_library.properties

import io.github.cameronward301.communication_scheduler.gateway_library.history.CommunicationHistoryAccessProvider
import io.github.cameronward301.communication_scheduler.gateway_library.service.ContentDeliveryService
import io.github.cameronward301.communication_scheduler.gateway_library.service.UserContentService
import spock.lang.Specification

class GatewayPropertiesTest extends Specification {
    def "should set gateway properties"() {
        given:
        CommunicationHistoryAccessProvider accessProvider = Mock(CommunicationHistoryAccessProvider)
        ContentDeliveryService contentDeliveryService = Mock(ContentDeliveryService)
        UserContentService userContentService = Mock(UserContentService)
        String userId = "test-user-id"
        String workflowRunId = "test-workflow-run-id"
        def gatewayProperties = GatewayPropertiesImpl.builder().build()

        when: "using setters"
        gatewayProperties.setCommunicationHistoryAccessProvider(accessProvider)
        gatewayProperties.setContentDeliveryService(contentDeliveryService)
        gatewayProperties.setUserContentService(userContentService)
        gatewayProperties.setUserId(userId)
        gatewayProperties.setWorkflowRunId(workflowRunId)


        then: "values are set"
        gatewayProperties.getCommunicationHistoryAccessProvider() == accessProvider
        gatewayProperties.getContentDeliveryService() == contentDeliveryService
        gatewayProperties.getUserContentService() == userContentService
        gatewayProperties.getUserId() == userId
        gatewayProperties.getWorkflowRunId() == workflowRunId
    }
}
