package io.github.cameronward301.communication_scheduler.gateway_library.properties

import spock.lang.Specification

class DefaultCommunicationHistoryAccessProviderPropertiesTest extends Specification {
    def "should set default communication history access provider properties"() {
        given:
        final String tableName = "test-table-name"
        final String region = "test-region"
        def defaultCommunicationHistoryAccessProviderProperties = new DefaultCommunicationHistoryAccessProviderProperties()

        when: "using setters"
        defaultCommunicationHistoryAccessProviderProperties.setTable_name(tableName)
        defaultCommunicationHistoryAccessProviderProperties.setRegion(region)

        then: "values are set"
        defaultCommunicationHistoryAccessProviderProperties.getTable_name() == tableName
        defaultCommunicationHistoryAccessProviderProperties.getRegion() == region
    }
}
