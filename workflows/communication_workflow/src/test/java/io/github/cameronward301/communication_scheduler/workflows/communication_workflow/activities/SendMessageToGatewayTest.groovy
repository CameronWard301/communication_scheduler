package io.github.cameronward301.communication_scheduler.workflows.communication_workflow.activities

import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.model.Preferences
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
    final String apiKey = "1234"
    String baseUrl
    String gatewayUrl
    Preferences preferences = new Preferences()

    public static MockWebServer mockWebServer

    def setup() {
        testActivityEnvironment = TestActivityEnvironment.newInstance(
                TestEnvironmentOptions.newBuilder()
                        .setUseTimeskipping(false)
                        .build()
        )

        mockWebServer = new MockWebServer()
        mockWebServer.start()

        WebClient webClient = WebClient.create()

        preferences.setGatewayTimeoutSeconds(1)

        testActivityEnvironment.registerActivitiesImplementations(new SendMessageToGatewayActivityImpl(webClient, apiKey))
        sendMessageToGatewayActivity = testActivityEnvironment.newActivityStub(SendMessageToGatewayActivity.class)
        baseUrl = String.format("http://localhost:%s", mockWebServer.getPort())
        gatewayUrl = baseUrl + "/test-gateway"
    }

    def cleanup() {
        testActivityEnvironment.close()
    }

    def "invoking gateway should return success, with userId and messageHash"() {
        given: "Mocked webClient returns success response"
        String messageHash = "test-hash"
        String responseJSON = "{\"userId\":\"" + USER_ID + "\",\"messageHash\":\"" + messageHash + "\"}"
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json").setBody(responseJSON))

        when: "sendMessageToGatewayActivity is invoked"
        Map<String, String> response = sendMessageToGatewayActivity.invokeGateway(USER_ID, WORKFLOW_RUN_ID, gatewayUrl, preferences)

        then: "response is success"
        response == Map.of("status", "complete", "userId", USER_ID, "messageHash", messageHash)
    }

    def "gateway responding with invalid content type should throw Activity Failure"() {
        given: "Mocked webClient returns invalid content type"
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).addHeader("Content-Type", "text/html;charset=UTF-8").setBody("{\"invalid-response\": \"true\"}"))

        when: "sendMessageToGatewayActivity is invoked"
        sendMessageToGatewayActivity.invokeGateway(USER_ID, WORKFLOW_RUN_ID, gatewayUrl, preferences)

        then: "exception is thrown"
        def exception = thrown(ActivityFailure)
        exception.originalMessage == "Gateway did not return content type of application/json"
    }

    def "gateway returning an invalid response should throw ActivityFailure"() {
        given: "Mocked webClient returns invalid response"
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json").setBody("{\"invalid-response\": \"true\"}"))

        when: "sendMessageToGatewayActivity is invoked"
        sendMessageToGatewayActivity.invokeGateway(USER_ID, WORKFLOW_RUN_ID, gatewayUrl, preferences)

        then: "exception is thrown"
        def exception = thrown(ActivityFailure)
        exception.originalMessage == "Gateway did not return a valid response"
    }

    def "invokeGateway should throw ActivityFailure when no response body is sent"() {
        given: "no body is returned from the gateway"
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json"))

        when: "sendMessageToGatewayActivity is invoked"
        sendMessageToGatewayActivity.invokeGateway(USER_ID, WORKFLOW_RUN_ID, gatewayUrl, preferences)

        then: "exception is thrown"
        def exception = thrown(ActivityFailure)
        exception.originalMessage == "Gateway did not return a valid response"
    }

    def "gateway returns invalid JSON should throw ActivityFailure"() {
        given: "Mocked webClient returns invalid JSON"
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json").setBody("invalid-json"))

        when: "sendMessageToGatewayActivity is invoked"
        sendMessageToGatewayActivity.invokeGateway(USER_ID, WORKFLOW_RUN_ID, gatewayUrl, preferences)

        then: "exception is thrown"
        def exception = thrown(ActivityFailure)
        exception.originalMessage == "Gateway did not return a valid response"
    }

    def "internal gateway error 500 should throw ActivityFailure"() {
        int responseCode = 500
        given: "Mocked webClient returns invalid response code 500"
        mockWebServer.enqueue(new MockResponse().setResponseCode(responseCode).addHeader("Content-Type", "application/json"))

        when: "sendMessageToGatewayActivity is invoked"
        sendMessageToGatewayActivity.invokeGateway(USER_ID, WORKFLOW_RUN_ID, gatewayUrl, preferences)

        then: "exception is thrown"
        def exception = thrown(ActivityFailure)
        exception.originalMessage == "Gateway unsuccessful, status: " + responseCode + " from: " + gatewayUrl
    }

    def "bad gateway request error 400 should throw ActivityFailure"() {
        int responseCode = 400
        given: "Mocked webClient returns invalid response code 400"
        mockWebServer.enqueue(new MockResponse().setResponseCode(responseCode).addHeader("Content-Type", "application/json"))

        when: "sendMessageToGatewayActivity is invoked"
        sendMessageToGatewayActivity.invokeGateway(USER_ID, WORKFLOW_RUN_ID, gatewayUrl, preferences)

        then: "exception is thrown"
        def exception = thrown(ActivityFailure)
        exception.originalMessage == "Gateway unsuccessful, status: " + responseCode + " from: " + gatewayUrl
    }

    def "unauthorised error 401 should throw ActivityFailure"() {
        int responseCode = 401
        given: "Mocked webClient returns invalid response code 401"
        mockWebServer.enqueue(new MockResponse().setResponseCode(responseCode).addHeader("Content-Type", "application/json"))

        when: "sendMessageToGatewayActivity is invoked"
        sendMessageToGatewayActivity.invokeGateway(USER_ID, WORKFLOW_RUN_ID, gatewayUrl, preferences)

        then: "exception is thrown"
        def exception = thrown(ActivityFailure)
        exception.originalMessage == "Gateway unsuccessful, status: " + responseCode + " from: " + gatewayUrl
    }

    def "forbidden error 403 should throw ActivityFailure"() {
        int responseCode = 403
        given: "Mocked webClient returns invalid response code 403"
        mockWebServer.enqueue(new MockResponse().setResponseCode(responseCode).addHeader("Content-Type", "application/json"))

        when: "sendMessageToGatewayActivity is invoked"
        sendMessageToGatewayActivity.invokeGateway(USER_ID, WORKFLOW_RUN_ID, gatewayUrl, preferences)

        then: "exception is thrown"
        def exception = thrown(ActivityFailure)
        exception.originalMessage == "Gateway unsuccessful, status: " + responseCode + " from: " + gatewayUrl
    }

    def "gateway timeout should throw ActivityFailure"() {
        given: "Mocked webClient responds after 10 seconds"
        mockWebServer.enqueue(new MockResponse().setHeadersDelay(10, TimeUnit.SECONDS))

        when: "sendMessageToGatewayActivity is invoked"
        sendMessageToGatewayActivity.invokeGateway(USER_ID, WORKFLOW_RUN_ID, gatewayUrl, preferences)

        then: "exception is thrown"
        def exception = thrown(ActivityFailure)
        exception.originalMessage == "Gateway timed out: " + gatewayUrl
    }

    def "invokeGateway should throw ActivityFailure when calling an invalid url"() {
        given: "url is invalid"
        String invalidUrl = "invalid-url"
        preferences.gatewayTimeoutSeconds = 10

        when: "sendMessageToGatewayActivity is invoked"
        sendMessageToGatewayActivity.invokeGateway(USER_ID, WORKFLOW_RUN_ID, invalidUrl, preferences)

        then: "exception is thrown"
        def exception = thrown(ActivityFailure)
        exception.originalMessage == "Could not invoke gateway: " + invalidUrl
    }


}
