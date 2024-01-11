package io.github.cameronward301.communication_scheduler.gateway_api.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;
import io.github.cameronward301.communication_scheduler.gateway_api.model.Gateway;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Repository
@RequiredArgsConstructor
public class GatewayRepository {
    private final DynamoDBMapper dynamoDBMapper;

    public List<Gateway> getAllGateways(Map<String, AttributeValue> startKey, int pageSize) {

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        scanExpression.withLimit(pageSize);
        scanExpression.withExclusiveStartKey(startKey);

        return dynamoDBMapper.scanPage(Gateway.class, scanExpression).getResults();
    }

    public List<Gateway> getGatewaysByQuery(String filterExpression, Map<String, AttributeValue> expressionValues, Map<String, AttributeValue> startKey, int pageSize) {
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        scanExpression.setFilterExpression(filterExpression);
        scanExpression.setExpressionAttributeValues(expressionValues);
        scanExpression.setExclusiveStartKey(startKey);
        scanExpression.setLimit(pageSize);

        return dynamoDBMapper.scanPage(Gateway.class, scanExpression).getResults();
    }

    public void save(Gateway gateway) {
        dynamoDBMapper.save(gateway);
    }

    public PaginatedQueryList<Gateway> findById(String id) {
        DynamoDBQueryExpression<Gateway> queryExpression = new DynamoDBQueryExpression<>();
        queryExpression.withHashKeyValues(Gateway.builder().id(id).build());

        return dynamoDBMapper.query(Gateway.class, queryExpression);
    }

    public void delete(Gateway gateway) {
        dynamoDBMapper.delete(gateway);
    }
}
