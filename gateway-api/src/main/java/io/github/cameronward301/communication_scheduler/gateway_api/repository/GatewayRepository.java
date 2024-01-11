package io.github.cameronward301.communication_scheduler.gateway_api.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import io.github.cameronward301.communication_scheduler.gateway_api.model.Gateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Repository layer for interacting with DynamoDB
 */
@Repository
@RequiredArgsConstructor
public class GatewayRepository {
    private final DynamoDBMapper dynamoDBMapper;

    /**
     * Gets all gateways from DynamoDB
     * @param startKey the id of the last gateway returned in the previous request, can be null
     * @param pageSize the number of gateways to return in this page
     * @return a list of gateways in the page
     */
    public List<Gateway> getAllGateways(Map<String, AttributeValue> startKey, int pageSize) {

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        scanExpression.withLimit(pageSize);
        scanExpression.withExclusiveStartKey(startKey);

        return dynamoDBMapper.scanPage(Gateway.class, scanExpression).getResults();
    }

    /**
     * Gets gateways from DynamoDB by query
     * @param filterExpression the filter expression to use
     * @param expressionValues the expression values to use
     * @param startKey the id of the last gateway returned in the previous request, can be null
     * @param pageSize the number of gateways to return in this page
     * @return a list of gateways matching the query
     */
    public List<Gateway> getGatewaysByQuery(String filterExpression, Map<String, AttributeValue> expressionValues, Map<String, AttributeValue> startKey, int pageSize) {
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        scanExpression.setFilterExpression(filterExpression);
        scanExpression.setExpressionAttributeValues(expressionValues);
        scanExpression.setExclusiveStartKey(startKey);
        scanExpression.setLimit(pageSize);

        return dynamoDBMapper.scanPage(Gateway.class, scanExpression).getResults();
    }

    /**
     * Creates a new gateway if the id does not exist or updates an existing gateway using the provided id in the Gateway model
     * @param gateway the gateway to save
     */
    public void save(Gateway gateway) {
        dynamoDBMapper.save(gateway);
    }

    /**
     * Gets a gateway by id
     * @param id the id of the gateway to get
     * @return a list of gateways matching the query (should only be one)
     */
    public PaginatedQueryList<Gateway> findById(String id) {
        DynamoDBQueryExpression<Gateway> queryExpression = new DynamoDBQueryExpression<>();
        queryExpression.withHashKeyValues(Gateway.builder().id(id).build());

        return dynamoDBMapper.query(Gateway.class, queryExpression);
    }

    /**
     * Deletes a gateway
     * @param gateway the gateway to delete from the id and dateCreated fields
     */
    public void delete(Gateway gateway) {
        dynamoDBMapper.delete(gateway);
    }
}
