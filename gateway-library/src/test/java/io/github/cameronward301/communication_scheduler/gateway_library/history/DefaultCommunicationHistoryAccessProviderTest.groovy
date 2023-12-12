package io.github.cameronward301.communication_scheduler.gateway_library.history

import io.github.cameronward301.communication_scheduler.gateway_library.helper.HashHelper
import io.github.cameronward301.communication_scheduler.gateway_library.model.CommunicationHistory
import io.github.cameronward301.communication_scheduler.gateway_library.properties.DefaultCommunicationHistoryAccessProviderProperties
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model.*
import spock.lang.Specification

import java.util.concurrent.CompletableFuture

class DefaultCommunicationHistoryAccessProviderTest extends Specification {
    private final DynamoDbAsyncClient dbAsyncClient = Mock(DynamoDbAsyncClient)
    private DefaultCommunicationHistoryAccessProvider accessProvider
    private DefaultCommunicationHistoryAccessProviderProperties properties

    def setup() {
        properties = new DefaultCommunicationHistoryAccessProviderProperties()
        properties.setTable_name("test-history-table")
        accessProvider = new DefaultCommunicationHistoryAccessProvider(dbAsyncClient, properties)
    }

    def "get history success for when message doesn't exist "() {
        given: "messageHash"
        String messageHash = HashHelper.messageHash("myId", "hello-content")

        and: "dynamoResponds with empty response"
        GetItemRequest getItemRequest = GetItemRequest.builder()
                .tableName(properties.getTable_name())
                .key(Map.of("message_hash", AttributeValue.builder().s(messageHash).build()))
                .build() as GetItemRequest

        GetItemResponse getItemResponse = GetItemResponse.builder()
                .build() as GetItemResponse

        dbAsyncClient.getItem(getItemRequest) >> CompletableFuture.completedFuture(getItemResponse)

        when: "get message by message hash is called"
        CommunicationHistory result = accessProvider.getPreviousCommunicationByMessageHash(messageHash)

        then: "correct response is returned"
        result.previousCommunicationMessageHash == messageHash
        !result.previousMessageSent
    }

    def "getHistory correct for when message already sent"() {
        given: "messageHash"
        String messageHash = HashHelper.messageHash("myId", "hello-content")

        and: "dynamoResponds with response"
        GetItemRequest getItemRequest = GetItemRequest.builder()
                .tableName(properties.getTable_name())
                .key(Map.of("message_hash", AttributeValue.builder().s(messageHash).build()))
                .build() as GetItemRequest

        GetItemResponse getItemResponse = GetItemResponse.builder()
                .item(Collections.singletonMap("previous_communication_message_hash", AttributeValue.builder()
                        .s(messageHash)
                        .build()
                ))
                .build() as GetItemResponse

        dbAsyncClient.getItem(getItemRequest) >> CompletableFuture.completedFuture(getItemResponse)

        when: "get message by message hash is called"
        CommunicationHistory result = accessProvider.getPreviousCommunicationByMessageHash(messageHash)

        then: "correct response is returned"
        result.getPreviousCommunicationMessageHash() == messageHash
        result.isPreviousMessageSent()
    }


    def "should remove communication history by message hash"() {
        given: "messageHash"
        String messageHash = HashHelper.messageHash("myId", "hello-content")

        and: "delete item response"
        DeleteItemResponse deleteItemResponse = DeleteItemResponse.builder()
                .build() as DeleteItemResponse

        when: "deleteByMessageHash is called"
        accessProvider.removeCommunicationHistoryByMessageHash(messageHash)

        then: "delete item is called once and returns response"
        1 * dbAsyncClient.deleteItem(_ as DeleteItemRequest) >> CompletableFuture.completedFuture(deleteItemResponse)
    }

    def "should handle error when removing communication history by message hash"() {
        given: "messageHash"
        String messageHash = HashHelper.messageHash("myId", "hello-content")

        and: "exception is"
        RuntimeException exception = new RuntimeException("test exception")

        and: "delete item is called and throws exception"
        CompletableFuture completableFuture = CompletableFuture.supplyAsync({ throw exception })
        1 * dbAsyncClient.deleteItem(_ as DeleteItemRequest) >> completableFuture

        when: "remove by message hash is called"
        accessProvider.removeCommunicationHistoryByMessageHash(messageHash)

        then: "exception is thrown"
        completableFuture.whenComplete { _, throwable ->

            assert throwable
            assert throwable == exception

        }

    }

    def "store communication in database"() {
        given: "details are set"
        String workflowId = "my-workflow-id"
        String userId = "user-id"
        String messageHash = HashHelper.messageHash(workflowId, "hello-content")


        and: "putItemResponse"
        PutItemResponse putItemResponse = PutItemResponse.builder()
                .attributes(Collections.singletonMap("messageHash", AttributeValue.builder().s(messageHash).build()))
                .build() as PutItemResponse


        when: "store communication is called"
        accessProvider.storeCommunication(messageHash, workflowId, userId)

        then: "put item is called once"
        1 * dbAsyncClient.putItem(_ as PutItemRequest) >> CompletableFuture.completedFuture(putItemResponse)
    }

    def "should handle error when storing communication history"() {
        given: "messageHash"
        String messageHash = HashHelper.messageHash("myId", "hello-content")

        and: "exception is"
        RuntimeException exception = new RuntimeException("test exception")

        and: "putItem is called and throws exception"
        CompletableFuture completableFuture = CompletableFuture.supplyAsync({ throw exception })
        1 * dbAsyncClient.putItem(_ as PutItemRequest) >> completableFuture

        when: "store history is called"
        accessProvider.storeCommunication("workflow-id", "user-id", messageHash)

        then: "exception is thrown"
        completableFuture.whenComplete { _, throwable ->
            assert throwable
            assert throwable == exception

        }
    }

    def "builder should create instance"() {
        given: "properties are set"
        DefaultCommunicationHistoryAccessProviderProperties properties = new DefaultCommunicationHistoryAccessProviderProperties()
        properties.setTable_name("test-history-table")

        when: "builder is called"
        DefaultCommunicationHistoryAccessProvider accessProvider = DefaultCommunicationHistoryAccessProvider.builder()
                .dynamoDbAsyncClient(dbAsyncClient)
                .historyAccessProviderProperties(properties)
                .build()

        then: "instance is created"
        accessProvider != null
    }
}
