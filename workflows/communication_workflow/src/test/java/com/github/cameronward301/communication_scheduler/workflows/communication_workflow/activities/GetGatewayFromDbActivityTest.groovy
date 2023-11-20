package com.github.cameronward301.communication_scheduler.workflows.communication_workflow.activities

import com.github.cameronward301.communication_scheduler.workflows.communication_workflow.properties.AwsProperties
import io.netty.util.concurrent.CompleteFuture
import io.temporal.testing.TestActivityEnvironment
import io.temporal.testing.TestEnvironmentOptions
import org.mockito.internal.matchers.Any
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.DynamoDbRequest
import software.amazon.awssdk.services.dynamodb.model.DynamoDbResponse
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse
import spock.lang.Specification

import java.util.concurrent.CompletableFuture

import static org.mockito.ArgumentMatchers.isA
import static org.mockito.Mockito.when

class GetGatewayFromDbActivityTest extends Specification {
    TestActivityEnvironment testActivityEnvironment
    DynamoDbAsyncClient dynamoDbAsyncClient = Mock(DynamoDbAsyncClient.class)
    GetGatewayFromDbActivity getGatewayFromDbActivity
    AwsProperties awsProperties


    def setup() {
        testActivityEnvironment = TestActivityEnvironment.newInstance(
                TestEnvironmentOptions.newBuilder()
                        .setUseTimeskipping(false)
                        .build()
        )

        awsProperties = new AwsProperties()
        awsProperties.setTable_name("test_table")
        awsProperties.setRegion("eu-west-1")
        awsProperties.setKey_name("id")

        testActivityEnvironment.registerActivitiesImplementations(new GetGatewayFromDbActivityImpl(awsProperties, dynamoDbAsyncClient))
        getGatewayFromDbActivity = testActivityEnvironment.newActivityStub(GetGatewayFromDbActivity.class)
    }

    def cleanup() {
        testActivityEnvironment.close()
    }

    def "getGatewayEndpointUrl returns correct url"() {
        given: "Mocked dynamoDbAsyncClient returns correct response"
        String expectedUrl = "https://test-gateway.com"
        DynamoDbResponse getItemResponse = GetItemResponse.builder()
                .item(Collections.singletonMap("endpoint_url", AttributeValue.builder().s(expectedUrl).build()))
                .build()

        GetItemRequest getItemRequest = GetItemRequest.builder()
                .tableName(awsProperties.getTable_name())
                .key(Map.of(awsProperties.getKey_name(), AttributeValue.builder().s("test-gateway").build()))
                .build() as GetItemRequest

        dynamoDbAsyncClient.getItem(getItemRequest) >> CompletableFuture.completedFuture(getItemResponse)

        when: "getGatewayEndpointUrl is called"
        def urlResult = getGatewayFromDbActivity.getGatewayEndpointUrl("test-gateway")

        then: "the correct url is returned"
        urlResult == expectedUrl
    }

}
