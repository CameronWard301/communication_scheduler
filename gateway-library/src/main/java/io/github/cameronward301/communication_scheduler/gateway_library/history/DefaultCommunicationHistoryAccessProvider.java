package io.github.cameronward301.communication_scheduler.gateway_library.history;

import io.github.cameronward301.communication_scheduler.gateway_library.model.CommunicationHistory;
import io.github.cameronward301.communication_scheduler.gateway_library.properties.DefaultCommunicationHistoryAccessProviderProperties;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Default Communication History Access Provider
 * Example implementation of CommunicationHistoryAccessProvider using DynamoDb as the data store
 */
@Builder
@Component
@Slf4j
public class DefaultCommunicationHistoryAccessProvider implements CommunicationHistoryAccessProvider {
    private final DynamoDbAsyncClient dynamoDbAsyncClient;
    private final DefaultCommunicationHistoryAccessProviderProperties historyAccessProviderProperties;

    /**
     * Constructor injecting dependencies
     *
     * @param dynamoDbAsyncClient             DynamoDbAsyncClient to use for communication with DynamoDb
     * @param historyAccessProviderProperties DynamoDb properties
     */
    public DefaultCommunicationHistoryAccessProvider(DynamoDbAsyncClient dynamoDbAsyncClient, DefaultCommunicationHistoryAccessProviderProperties historyAccessProviderProperties) {
        this.dynamoDbAsyncClient = dynamoDbAsyncClient;
        this.historyAccessProviderProperties = historyAccessProviderProperties;
    }

    @Override
    public CommunicationHistory getPreviousCommunicationByMessageHash(String messageHash) {
        GetItemRequest getItemRequest = GetItemRequest.builder()
                .tableName(historyAccessProviderProperties.getTable_name())
                .key(Map.of("message_hash", AttributeValue.builder().s(messageHash).build()))
                .build();

        log.debug("Sending dynamoDb request: {}", getItemRequest);
        GetItemResponse getItemResponse = dynamoDbAsyncClient.getItem(getItemRequest).join();
        log.debug("Retrieved dynamoDb response: {}", getItemResponse);

        if (getItemResponse.hasItem()) {
            log.debug("Previous communication found for message hash: {}", messageHash);
            return CommunicationHistory.builder()
                    .previousMessageSent(true)
                    .previousCommunicationMessageHash(getItemResponse.item().get("message_hash").s())
                    .build();
        }
        log.debug("No previous communication found for message hash: {}", messageHash);
        return CommunicationHistory.builder()
                .previousMessageSent(false)
                .previousCommunicationMessageHash(messageHash)
                .build();
    }

    @Override
    public void removeCommunicationHistoryByMessageHash(String messageHash) {
        DeleteItemRequest deleteItemRequest = DeleteItemRequest.builder()
                .tableName(historyAccessProviderProperties.getTable_name())
                .key(Map.of("message_hash", AttributeValue.builder().s(messageHash).build()))
                .build();

        log.debug("Sending dynamoDb request: {}", deleteItemRequest);
        CompletableFuture<DeleteItemResponse> deleteBackupResponseCompletableFuture = dynamoDbAsyncClient.deleteItem(deleteItemRequest);

        deleteBackupResponseCompletableFuture.whenComplete((deleteItemResponse, throwable) -> {
            if (throwable != null) {
                log.error("Error removing communication history from dynamoDb", throwable);
                throw new RuntimeException(throwable);
            } else {
                log.debug("Retrieved dynamoDb response: {}", deleteItemResponse);
            }
        });
    }

    @Override
    public void storeCommunication(String workflowRunId, String userId, String messageHash) {
        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(historyAccessProviderProperties.getTable_name())
                .item(Map.of(
                        "message_hash", AttributeValue.builder().s(messageHash).build(),
                        "workflow_run_id", AttributeValue.builder().s(workflowRunId).build(),
                        "user_id", AttributeValue.builder().s(userId).build()
                ))
                .build();

        log.debug("Sending dynamoDb request: {}", putItemRequest);
        CompletableFuture<PutItemResponse> itemResponseCompletableFuture = dynamoDbAsyncClient.putItem(putItemRequest);

        itemResponseCompletableFuture.whenComplete((putItemResponse, throwable) -> {
            if (throwable != null) {
                log.error("Error storing communication in dynamoDb", throwable);
                throw new RuntimeException(throwable);
            } else {
                log.debug("Retrieved dynamoDb response: {}", putItemResponse);
            }
        });

    }
}
