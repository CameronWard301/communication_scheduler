package com.github.cameronward301.communication_scheduler.workflows.communication_workflow.activities

import com.github.cameronward301.communication_scheduler.workflows.communication_workflow.properties.ActivityProperties
import io.temporal.failure.ActivityFailure
import io.temporal.testing.TestActivityEnvironment
import io.temporal.testing.TestEnvironmentOptions
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.springframework.web.reactive.function.client.WebClient
import spock.lang.Specification

import java.util.concurrent.TimeUnit


class SendMessageToGatewayTest extends Specification {
    TestActivityEnvironment testActivityEnvironment
    SendMessageToGatewayActivity sendMessageToGatewayActivity
    final String USER_ID = "test-user"
    final String WORKFLOW_RUN_ID = "test-run-id"
    String baseUrl
    String gatewayUrl
    ActivityProperties activityProperties = new ActivityProperties()

    public static MockWebServer mockWebServer;

    def setup() {
        testActivityEnvironment = TestActivityEnvironment.newInstance(
                TestEnvironmentOptions.newBuilder()
                        .setUseTimeskipping(false)
                        .build()
        )

        mockWebServer = new MockWebServer()
        mockWebServer.start()

        WebClient webClient = WebClient.create()

        activityProperties.setGateway_timeout_seconds(1)

        testActivityEnvironment.registerActivitiesImplementations(new SendMessageToGatewayActivityImpl(webClient, activityProperties))
        sendMessageToGatewayActivity = testActivityEnvironment.newActivityStub(SendMessageToGatewayActivity.class)
        baseUrl = String.format("http://localhost:%s", mockWebServer.getPort())
        gatewayUrl = baseUrl + "/test-gateway"
    }

    def cleanup() {
        testActivityEnvironment.close()
    }

    def "invoking gateway should return success"() {
        given: "Mocked webClient returns success"
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json"))

        when: "sendMessageToGatewayActivity is invoked"
        Map<String, String> response = sendMessageToGatewayActivity.invokeGateway(USER_ID, WORKFLOW_RUN_ID, gatewayUrl)

        then: "response is success"
        response == Map.of("status", "complete", "userId", USER_ID, "gateway_url", gatewayUrl);
    }

    def "internal gateway error 500 should throw ActivityFailure"() {
        int responseCode = 500
        given: "Mocked webClient returns invalid response code 500"
        mockWebServer.enqueue(new MockResponse().setResponseCode(responseCode).addHeader("Content-Type", "application/json"))

        when: "sendMessageToGatewayActivity is invoked"
        sendMessageToGatewayActivity.invokeGateway(USER_ID, WORKFLOW_RUN_ID, gatewayUrl)

        then: "exception is thrown"
        def exception = thrown(ActivityFailure)
        exception.originalMessage == "Gateway unsuccessful, status: " + responseCode + " from: " + gatewayUrl
    }

    def "bad gateway request error 400 should throw ActivityFailure"() {
        int responseCode = 400
        given: "Mocked webClient returns invalid response code 400"
        mockWebServer.enqueue(new MockResponse().setResponseCode(responseCode).addHeader("Content-Type", "application/json"))

        when: "sendMessageToGatewayActivity is invoked"
        sendMessageToGatewayActivity.invokeGateway(USER_ID, WORKFLOW_RUN_ID, gatewayUrl)

        then: "exception is thrown"
        def exception = thrown(ActivityFailure)
        exception.originalMessage == "Gateway unsuccessful, status: " + responseCode + " from: " + gatewayUrl
    }

    def "unauthorised error 401 should throw ActivityFailure"() {
        int responseCode = 401
        given: "Mocked webClient returns invalid response code 401"
        mockWebServer.enqueue(new MockResponse().setResponseCode(responseCode).addHeader("Content-Type", "application/json"))

        when: "sendMessageToGatewayActivity is invoked"
        sendMessageToGatewayActivity.invokeGateway(USER_ID, WORKFLOW_RUN_ID, gatewayUrl)

        then: "exception is thrown"
        def exception = thrown(ActivityFailure)
        exception.originalMessage == "Gateway unsuccessful, status: " + responseCode + " from: " + gatewayUrl
    }

    def "gateway timeout should throw ActivityFailure"() {
        given: "Mocked webClient responds after 10 seconds"
        mockWebServer.enqueue(new MockResponse().setHeadersDelay(10, TimeUnit.SECONDS))

        when: "sendMessageToGatewayActivity is invoked"
        sendMessageToGatewayActivity.invokeGateway(USER_ID, WORKFLOW_RUN_ID, gatewayUrl)

        then: "exception is thrown"
        def exception = thrown(ActivityFailure)
        exception.originalMessage == "Gateway timed out: " + gatewayUrl
    }

    def "invokeGateway should throw ActivityFailure when calling an invalid url"() {
        given: "url is invalid"
        String invalidUrl = "invalid-url"

        when: "sendMessageToGatewayActivity is invoked"
        sendMessageToGatewayActivity.invokeGateway(USER_ID, WORKFLOW_RUN_ID, invalidUrl)

        then: "exception is thrown"
        def exception = thrown(ActivityFailure)
        exception.originalMessage == "Could not invoke gateway: " + invalidUrl
    }
}
