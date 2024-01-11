package io.github.cameronward301.communication_scheduler.gateway_api.service

import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import io.github.cameronward301.communication_scheduler.gateway_api.exception.RequestException
import io.github.cameronward301.communication_scheduler.gateway_api.model.Gateway
import io.github.cameronward301.communication_scheduler.gateway_api.repository.GatewayRepository
import org.springframework.http.HttpStatus
import spock.lang.Specification

class GatewayServiceTest extends Specification {
    private GatewayService gatewayService
    private GatewayRepository gatewayRepository = Mock(GatewayRepository)
    private Gateway testGateway = Gateway.builder().id("test-id")
            .friendlyName("test-friendly-name")
            .description("test-description")
            .dateCreated("test-date-created")
            .build()

    def setup() {
        gatewayService = new GatewayService(gatewayRepository)
    }

    def "should return a list of gateways when getAllGateways is called"() {
        given: "Request parameters"
        def pageSize = 2
        def startKey = null
        def friendlyName = null
        def endpointUrl = null

        and: "GatewayRepository returns a list of gateways"
        gatewayRepository.getAllGateways(startKey, pageSize) >> List<Gateway>.of(testGateway, testGateway)

        when:
        def response = gatewayService.getGateways(startKey, pageSize, friendlyName, endpointUrl)

        then:
        response.size() == 2
    }

    def "should return a list of gateways when getAllGateways is called with start key"() {
        given: "Request parameters"
        def pageSize = 2
        def startKey = "1234"
        def friendlyName = null
        def endpointUrl = null

        and: "GatewayRepository returns a list of gateways"
        gatewayRepository.getAllGateways(Map<String, AttributeValue>.of("id", new AttributeValue(startKey)), pageSize) >> List<Gateway>.of(testGateway, testGateway)

        when:
        def response = gatewayService.getGateways(startKey, pageSize, friendlyName, endpointUrl)

        then:
        response.size() == 2
    }

    def "should return a list of gateways when getAllGateways is called with friendly name"() {
        given: "Request parameters"
        def pageSize = 2
        def startKey = null
        def friendlyName = "test-friendly-name"
        def endpointUrl = null

        and: "expression string is"
        def expressionString = "contains(friendly_name, :friendlyName)"

        and: "expression values are"
        def expressionValues = Map<String, AttributeValue>.of(":friendlyName", new AttributeValue(friendlyName))

        and: "GatewayRepository returns a list of gateways"
        gatewayRepository.getGatewaysByQuery(expressionString, expressionValues, startKey, pageSize) >> List<Gateway>.of(testGateway, testGateway)

        when:
        def response = gatewayService.getGateways(startKey, pageSize, friendlyName, endpointUrl)

        then:
        response.size() == 2
    }

    def "should return a list of gateways when getAllGateways is called with endpoint url"() {
        given: "Request parameters"
        def pageSize = 2
        def startKey = null
        def friendlyName = null
        def endpointUrl = "test-endpoint-url"

        and: "expression string is"
        def expressionString = "contains(endpoint_url, :endpointUrl)"

        and: "expression values are"
        def expressionValues = Map<String, AttributeValue>.of(":endpointUrl", new AttributeValue(endpointUrl))

        and: "GatewayRepository returns a list of gateways"
        gatewayRepository.getGatewaysByQuery(expressionString, expressionValues, startKey, pageSize) >> List<Gateway>.of(testGateway, testGateway)

        when:
        def response = gatewayService.getGateways(startKey, pageSize, friendlyName, endpointUrl)

        then:
        response.size() == 2
    }

    def "should return a list of gateways when getAllGateways is called with endpoint url and friendly name"() {
        given: "Request parameters"
        def pageSize = 2
        def startKey = null
        def friendlyName = "test-friendly-name"
        def endpointUrl = "test-endpoint-url"

        and: "expression string is"
        def expressionString = "contains(friendly_name, :friendlyName) and contains(endpoint_url, :endpointUrl)"

        and: "expression values are"
        def expressionValues = Map<String, AttributeValue>.of(":friendlyName", new AttributeValue().withS(friendlyName), ":endpointUrl", new AttributeValue().withS(endpointUrl))

        and: "GatewayRepository returns a list of gateways"
        gatewayRepository.getGatewaysByQuery(expressionString, expressionValues, startKey, pageSize) >> List<Gateway>.of(testGateway, testGateway)

        when:
        def response = gatewayService.getGateways(startKey, pageSize, friendlyName, endpointUrl)

        then:
        response.size() == 2
    }

    def "create gateway should create gateway in correct format"() {
        given: "Gateway request"
        def gatewayRequest = Gateway.builder()
                .friendlyName("Test-friendly-name")
                .description("Test-description")
                .endpointUrl("test-endpoint-url")
                .build()

        and: "GatewayRepository creates gateway"
        gatewayRepository.save(gatewayRequest) >> {}

        when:
        def response = gatewayService.createGateway(gatewayRequest)

        then:
        response.dateCreated != null
        response.id != null
        response.friendlyName == gatewayRequest.friendlyName.toLowerCase()
        response.description == gatewayRequest.description.toLowerCase()
    }

    def "Get gatewayById should return gateway"() {
        given: "Gateway id"
        def gatewayId = testGateway.getId()

        and: "GatewayRepository returns gateway"
        PaginatedQueryList<Gateway> mockList = Mock(PaginatedQueryList)
        1 * mockList.isEmpty() >> false
        1 * mockList.getFirst() >> testGateway
        gatewayRepository.findById(gatewayId) >> mockList

        when:
        def response = gatewayService.getGatewayById(gatewayId)

        then:
        response.id == gatewayId
    }

    def "Get gatewayById should throw RequestException if no results returned"() {
        given: "Gateway id"
        def gatewayId = testGateway.getId()

        and: "GatewayRepository returns no results"
        PaginatedQueryList<Gateway> mockList = Mock(PaginatedQueryList)
        1 * mockList.isEmpty() >> true
        gatewayRepository.findById(gatewayId) >> mockList

        when:
        gatewayService.getGatewayById(gatewayId)

        then:
        def exception = thrown(RequestException)

        and:
        exception.getMessage() == "Gateway with id '$gatewayId' not found"
        exception.getHttpStatus() == HttpStatus.NOT_FOUND
    }

    def "Delete gatewayById should complete successfully"() {
        given: "Gateway id"
        def gatewayId = testGateway.getId()

        and: "GatewayRepository returns gateway"
        PaginatedQueryList<Gateway> mockList = Mock(PaginatedQueryList)
        1 * mockList.isEmpty() >> false
        1 * mockList.getFirst() >> testGateway
        gatewayRepository.findById(gatewayId) >> mockList

        and: "GatewayRepository deletes gateway"
        gatewayRepository.delete(testGateway) >> {}

        when:
        gatewayService.deleteGatewayById(gatewayId)

        then:
        notThrown(Exception)
    }

    def "Delete gatewayById should throw RequestException if can't find gateway by id"() {
        given: "Gateway id"
        def gatewayId = testGateway.getId()

        and: "GatewayRepository returns no results"
        PaginatedQueryList<Gateway> mockList = Mock(PaginatedQueryList)
        1 * mockList.isEmpty() >> true
        gatewayRepository.findById(gatewayId) >> mockList

        when:
        gatewayService.deleteGatewayById(gatewayId)

        then:
        def exception = thrown(RequestException)

        and:
        exception.getMessage() == "Gateway with id '$gatewayId' not found"
        exception.getHttpStatus() == HttpStatus.NOT_FOUND

        and:
        0 * gatewayRepository.delete(testGateway)
    }

    def "Update gateway should update gateway in correct format"() {
        given: "Gateway request"
        def gatewayRequest = Gateway.builder()
                .id(testGateway.getId())
                .friendlyName("Updated-test-friendly-name")
                .description("Updated-test-description")
                .dateCreated("should-not-be-updated")
                .endpointUrl("updated-test-endpoint-url")
                .build()

        and: "FindById returns gateway"
        PaginatedQueryList<Gateway> mockList = Mock(PaginatedQueryList)
        1 * mockList.isEmpty() >> false
        1 * mockList.getFirst() >> testGateway
        gatewayRepository.findById(testGateway.getId()) >> mockList

        and: "GatewayRepository updates gateway"
        gatewayRepository.save(gatewayRequest) >> {}

        when:
        def response = gatewayService.updateGateway(gatewayRequest)

        then:
        response.dateCreated == testGateway.getDateCreated()
        response.id == testGateway.getId()
        response.friendlyName == gatewayRequest.friendlyName.toLowerCase()
        response.description == gatewayRequest.description.toLowerCase()
        response.endpointUrl == gatewayRequest.endpointUrl.toLowerCase()
    }

    def "Update gateway should throw RequestException if can't find gateway by id"() {
        given: "Gateway request"
        def gatewayRequest = Gateway.builder()
                .id(testGateway.getId())
                .friendlyName("Updated-test-friendly-name")
                .description("Updated-test-description")
                .dateCreated("should-not-be-updated")
                .endpointUrl("updated-test-endpoint-url")
                .build()

        and: "FindById returns no results"
        PaginatedQueryList<Gateway> mockList = Mock(PaginatedQueryList)
        1 * mockList.isEmpty() >> true
        gatewayRepository.findById(testGateway.getId()) >> mockList

        when:
        gatewayService.updateGateway(gatewayRequest)

        then:
        def exception = thrown(RequestException)

        and:
        exception.getMessage() == "Gateway with id '${testGateway.getId()}' not found"
        exception.getHttpStatus() == HttpStatus.NOT_FOUND

        and:
        0 * gatewayRepository.save(gatewayRequest)
    }

    def "should throw RequestException with status code 400 when id is missing from the update request"() {
        given: "Gateway request"
        def gatewayRequest = Gateway.builder()
                .friendlyName("Updated-test-friendly-name")
                .description("Updated-test-description")
                .dateCreated("should-not-be-updated")
                .endpointUrl("updated-test-endpoint-url")
                .build()

        when:
        gatewayService.updateGateway(gatewayRequest)

        then:
        def exception = thrown(RequestException)

        and:
        exception.getMessage() == "Please provide a gateway 'id' in the request body"
        exception.getHttpStatus() == HttpStatus.BAD_REQUEST

        and:
        0 * gatewayRepository.save(gatewayRequest)
    }

}
