package com.github.cameronward301.communication_scheduler.workflows.communication_workflow.activities;

import com.github.cameronward301.communication_scheduler.workflows.communication_workflow.properties.AwsProperties;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;

import java.util.Map;

/**
 * Get Gateway From DynamoDb Implementation
 */
@Slf4j
public class GetGatewayFromDbActivityImpl implements GetGatewayFromDbActivity {

    private final DynamoDbAsyncClient dynamoDbClient;
    private final AwsProperties awsProperties;

    public GetGatewayFromDbActivityImpl(AwsProperties awsProperties, DynamoDbAsyncClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
        this.awsProperties = awsProperties;
    }

    @Override
    public String getGatewayEndpointUrl(String gatewayId) {
        GetItemRequest getItemRequest = GetItemRequest.builder()
                .tableName(awsProperties.getTable_name())
                .key(Map.of(awsProperties.getKey_name(), AttributeValue.builder().s(gatewayId).build()))
                .build();

        log.debug("Sending dynamoDb request: {}", getItemRequest);
        GetItemResponse getItemResponse = dynamoDbClient.getItem(getItemRequest).join();
        log.debug("Retrieved dynamoDb response: {}", getItemResponse);

        return getItemResponse.item().get("endpoint_url").s();
    }
}
