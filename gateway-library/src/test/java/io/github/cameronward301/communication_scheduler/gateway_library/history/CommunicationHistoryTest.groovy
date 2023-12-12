package io.github.cameronward301.communication_scheduler.gateway_library.history

import io.github.cameronward301.communication_scheduler.gateway_library.model.CommunicationHistory
import spock.lang.Specification

class CommunicationHistoryTest extends Specification {
    def "test CommunicationHistory setters"() {
        given:
        String messageHash = "test-hash"
        boolean messageSent = true
        def communicationHistory = CommunicationHistory.builder().build()

        when: "using setters"
        communicationHistory.setPreviousCommunicationMessageHash(messageHash)
        communicationHistory.setPreviousMessageSent(messageSent)

        then: "values are set"
        communicationHistory.getPreviousCommunicationMessageHash() == messageHash
        communicationHistory.isPreviousMessageSent() == messageSent

    }

}
