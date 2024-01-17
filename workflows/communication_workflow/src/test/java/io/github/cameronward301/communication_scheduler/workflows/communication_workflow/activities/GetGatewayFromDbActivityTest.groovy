package io.github.cameronward301.communication_scheduler.workflows.communication_workflow.activities


import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.model.Gateway

import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.repository.GatewayRepository
import io.temporal.failure.ActivityFailure
import io.temporal.testing.TestActivityEnvironment
import io.temporal.testing.TestEnvironmentOptions
import spock.lang.Specification

class GetGatewayFromDbActivityTest extends Specification {
    TestActivityEnvironment testActivityEnvironment
    GatewayRepository gatewayRepository = Mock(GatewayRepository)
    GetGatewayFromDbActivity getGatewayFromDbActivity


    def setup() {
        testActivityEnvironment = TestActivityEnvironment.newInstance(
                TestEnvironmentOptions.newBuilder()
                        .setUseTimeskipping(false)
                        .build()
        )

        testActivityEnvironment.registerActivitiesImplementations(new GetGatewayFromDbActivityImpl(gatewayRepository))
        getGatewayFromDbActivity = testActivityEnvironment.newActivityStub(GetGatewayFromDbActivity.class)
    }

    def cleanup() {
        testActivityEnvironment.close()
    }

    def "getGatewayEndpointUrl returns correct url"() {
        given: "Mocked dynamoDbAsyncClient returns correct response"
        String expectedUrl = "https://test-gateway.com"

        gatewayRepository.findById("test-gateway") >> Optional.of(Gateway.builder().endpointUrl(expectedUrl).build())

        when: "getGatewayEndpointUrl is called"
        def urlResult = getGatewayFromDbActivity.getGatewayEndpointUrl("test-gateway")

        then: "the correct url is returned"
        urlResult == expectedUrl
    }

    def "getGatewayEndpointUrl throws GatewayNotFoundException if gateway does not exist"() {
        given: "Mocked dynamoDbAsyncClient returns correct response"

        gatewayRepository.findById("test-gateway") >> Optional.empty()

        when: "getGatewayEndpointUrl is called"
        getGatewayFromDbActivity.getGatewayEndpointUrl("test-gateway")


        then: "the GatewayNotFoundException is thrown"
        def exception = thrown(ActivityFailure)
        exception.getOriginalMessage() == "Gateway with id test-gateway not found"
    }

}
