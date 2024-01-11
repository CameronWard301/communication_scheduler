package io.github.cameronward301.communication_scheduler.gateway_api.repository

import com.amazonaws.services.dynamodbv2.datamodeling.*
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import io.github.cameronward301.communication_scheduler.gateway_api.model.Gateway
import spock.lang.Specification

class GatewayRepositoryTest extends Specification {
    private DynamoDBMapper dynamoDBMapper = Mock(DynamoDBMapper)
    private GatewayRepository gatewayRepository
    private final Gateway testGateway = Gateway.builder().id("test-id")
            .friendlyName("test-friendly-name")
            .description("test-description")
            .dateCreated("test-date-created")
            .build()

    def setup() {
        gatewayRepository = new GatewayRepository(dynamoDBMapper)
    }

    def "should save gateway with no exception"() {
        given:
        def gateway = Gateway.builder()
                .friendlyName("test-friendly-name")
                .description("test-description")
                .build()

        and:
        dynamoDBMapper.save(gateway) >> {}

        when:
        gatewayRepository.save(gateway)

        then:
        noExceptionThrown()
    }

    def "Should delete gateway with no exception"() {
        given: "DynamoDBMapper deletes gateway"
        dynamoDBMapper.delete(testGateway) >> {}

        when:
        gatewayRepository.delete(testGateway)

        then:
        noExceptionThrown()
    }

    def "should return a list of gateways when providing a start key and page size"() {
        given: "input parameters"
        def pageSize = 2

        and:
        ScanResultPage<Gateway> resultPage = Mock(ScanResultPage)
        resultPage.getResults() >> List.of(testGateway)
        dynamoDBMapper.scanPage(Gateway.class, _ as DynamoDBScanExpression) >> resultPage

        when: "getAllGateways is called"
        def results = gatewayRepository.getAllGateways(null, pageSize)

        then:
        results.size() == 1
    }

    def "should return a list of gateways for the given query"() {
        given: "Input args"
        String filterExpression = "test-filter-expression"
        Map<String, AttributeValue> expressionValues = Map<String, AttributeValue>.of("id", new AttributeValue("test-id-2"))
        Map<String, AttributeValue> startKey = Map<String, AttributeValue>.of("id", new AttributeValue("test-id-1"))
        int pageSize = 5

        and: "Scan Expression"
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
        scanExpression.setFilterExpression(filterExpression)
        scanExpression.setExpressionAttributeValues(expressionValues)
        scanExpression.setExclusiveStartKey(startKey)
        scanExpression.setLimit(pageSize)

        and: "mapper returns list of gateways"
        ScanResultPage<Gateway> resultPage = Mock(ScanResultPage)
        resultPage.getResults() >> List.of(testGateway)
        dynamoDBMapper.scanPage(Gateway.class, _ as DynamoDBScanExpression) >> resultPage

        when:
        def result = gatewayRepository.getGatewaysByQuery(filterExpression, expressionValues, startKey, pageSize)

        then:
        noExceptionThrown()

        and:
        result.size() == 1
    }

    def "should return a paginated query list of gateways when finding by id"() {
        given:
        def testId = testGateway.getId()

        and: "mapper returns gateway"
        PaginatedQueryList<Gateway> result = Mock(PaginatedQueryList)
        result.getFirst() >> testGateway
        dynamoDBMapper.query(Gateway.class, _ as DynamoDBQueryExpression) >> result

        when:
        def response = gatewayRepository.findById(testId)

        then:
        response.getFirst() == testGateway

        and:
        noExceptionThrown()

    }
}
