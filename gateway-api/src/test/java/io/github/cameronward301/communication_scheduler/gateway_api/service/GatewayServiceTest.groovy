package io.github.cameronward301.communication_scheduler.gateway_api.service

import io.github.cameronward301.communication_scheduler.gateway_api.exception.RequestException
import io.github.cameronward301.communication_scheduler.gateway_api.model.Gateway
import io.github.cameronward301.communication_scheduler.gateway_api.repository.GatewayRepository
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
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

    def "should return a list of gateways when findAll is called"() {
        given: "Request parameters"
        def pageSize = "2"
        def pageNumber = "0"
        def sortField = "dateCreated"
        def sortDirection = "desc"

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField)

        and: "GatewayRepository returns a list of gateways"
        gatewayRepository.findAll(PageRequest.of(
                pageNumber == null ? 0 : Integer.parseInt(pageNumber),
                pageSize == null ? 0 : Integer.parseInt(pageSize),
                sort
        )) >> new PageImpl<Gateway>(List.of(testGateway, testGateway))

        when:
        def response = gatewayService.getGateways(pageNumber, pageSize, null, null, null, sortField, sortDirection)

        then:
        response.size() == 2
    }

    def "Should throw exception when findAll is called with invalid sort direction"() {
        given: "Request parameters"
        def pageSize = "2"
        def pageNumber = "0"
        def sortField = "dateCreated"
        def sortDirection = "invalid"

        when:
        gatewayService.getGateways(pageNumber, pageSize, null, null, null, sortField, sortDirection)

        then:
        def exception = thrown(RequestException)

        and:
        exception.getMessage() == "Invalid sort sortDirection: '" + sortDirection + "', must be asc or desc"
        exception.getHttpStatus() == HttpStatus.BAD_REQUEST
    }

    def "Should be able to set the sort direction to ascending when getting all gateways"(){
        given: "Request parameters"
        def pageSize = "2"
        def pageNumber = "0"
        def sortField = "dateCreated"
        def sortDirection = "asc"

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField)

        and: "GatewayRepository returns a list of gateways"
        gatewayRepository.findAll(PageRequest.of(
                pageNumber == null ? 0 : Integer.parseInt(pageNumber),
                pageSize == null ? 0 : Integer.parseInt(pageSize),
                sort
        )) >> new PageImpl<Gateway>(List.of(testGateway, testGateway))

        when:
        def response = gatewayService.getGateways(pageNumber, pageSize, null, null, null, sortField, sortDirection)

        then:
        response.size() == 2
    }

    def "Should return a page of gateways when filtering friendlyName, endpointUrl and description"(){
        given: "Request parameters"
        def pageSize = "2"
        def pageNumber = "0"
        def sortField = "dateCreated"
        def sortDirection = "desc"
        def friendlyName = "test-friendly-name"
        def endpointUrl = "test-endpoint-url"
        def description = "test-description"

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField)

        and: "GatewayRepository returns a list of gateways"
        gatewayRepository.findByFriendlyNameRegexAndEndpointUrlRegexAndDescriptionRegex(
                friendlyName, endpointUrl, description, PageRequest.of(
                        pageNumber == null ? 0 : Integer.parseInt(pageNumber),
                        pageSize == null ? 0 : Integer.parseInt(pageSize),
                        sort
                )
        ) >> new PageImpl<Gateway>(List.of(testGateway, testGateway))

        when:
        def response = gatewayService.getGateways(pageNumber, pageSize, friendlyName, endpointUrl, description, sortField, sortDirection)

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

    def "create gateway without description should set field to empty string"(){
        given: "Gateway request"
        def gatewayRequest = Gateway.builder()
                .friendlyName("Test-friendly-name")
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
        response.description == ""
    }

    def "Get gatewayById should return gateway"() {
        given: "Gateway id"
        def gatewayId = testGateway.getId()

        and: "GatewayRepository returns gateway"
        gatewayRepository.findById(gatewayId) >> Optional.of(testGateway)

        when:
        def response = gatewayService.getGatewayById(gatewayId)

        then:
        response.id == gatewayId
    }

    def "Get gatewayById should throw RequestException if no results returned"() {
        given: "Gateway id"
        def gatewayId = testGateway.getId()

        and: "GatewayRepository returns no results"
        gatewayRepository.findById(gatewayId) >> Optional.empty()

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
        gatewayRepository.findById(gatewayId) >> Optional.of(testGateway)

        and: "GatewayRepository deletes gateway"
        gatewayRepository.deleteById(testGateway.getId()) >> {}

        when:
        gatewayService.deleteGatewayById(gatewayId)

        then:
        notThrown(Exception)
    }

    def "Delete gatewayById should throw RequestException if can't find gateway by id"() {
        given: "Gateway id"
        def gatewayId = testGateway.getId()

        and: "GatewayRepository returns no results"
        gatewayRepository.findById(gatewayId) >> Optional.empty()

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
        gatewayRepository.findById(testGateway.getId()) >> Optional.of(testGateway)

        and: "GatewayRepository updates gateway"
        gatewayRepository.save(_ as Gateway) >> Gateway.builder()
                .id(testGateway.getId())
                .friendlyName(gatewayRequest.friendlyName.toLowerCase())
                .description(gatewayRequest.description.toLowerCase())
                .dateCreated(testGateway.getDateCreated())
                .endpointUrl(gatewayRequest.endpointUrl)
                .build()

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
        gatewayRepository.findById(testGateway.getId()) >> Optional.empty()

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
