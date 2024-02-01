package io.github.cameronward301.communication_scheduler.gateway_api.controller

import io.github.cameronward301.communication_scheduler.gateway_api.exception.RequestException
import io.github.cameronward301.communication_scheduler.gateway_api.model.Gateway
import io.github.cameronward301.communication_scheduler.gateway_api.service.GatewayService
import org.springframework.data.domain.PageImpl
import org.springframework.http.HttpStatus
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import spock.lang.Specification

class GatewayControllerTest extends Specification {

    private GatewayController gatewayController
    private GatewayService gatewayService = Mock(GatewayService)
    private Gateway testGateway = Gateway.builder().id("test-id")
            .friendlyName("test-friendly-name")
            .description("test-description")
            .dateCreated("test-date-created")
            .build()

    def setup() {
        gatewayController = new GatewayController(gatewayService)
    }

    def "should return 200 when (GET) getAllGateways is called"() {
        given: "Request parameters"
        def pageSize = "2"
        def pageNumber = "0"
        def friendlyName = null
        def gatewayUrl = null
        def description = null
        def sortField = null
        def sortDirection = null

        and: "GatewayService returns a response"
        gatewayService.getGateways(pageNumber, pageSize, friendlyName, gatewayUrl, description, sortField, sortDirection) >> new PageImpl<Gateway>(List.of(testGateway, testGateway))

        when:
        def response = gatewayController.getAllGateways(pageNumber, pageSize, friendlyName, gatewayUrl, description, sortField, sortDirection)

        then:
        response.getStatusCode() == HttpStatus.OK

        and:
        response.getBody().size() == 2
    }

    def "Should return 201 when create (POST) gateway is called"() {
        given: "Gateway service creates gateway"
        gatewayService.createGateway(testGateway) >> testGateway

        and: "Binding result returns no errors"
        def bindingResult = Mock(BindingResult)
        bindingResult.hasErrors() >> false

        when:
        def response = gatewayController.createGateway(testGateway, bindingResult)

        then:
        response.getStatusCode() == HttpStatus.CREATED
    }

    def "Should throw RequestException with status code 400 when create (POST) gateway is called with invalid request body"() {
        given: "Gateway service creates gateway"
        gatewayService.createGateway(testGateway) >> testGateway

        and: "Binding result returns errors"
        def bindingResult = Mock(BindingResult)
        bindingResult.hasErrors() >> true
        FieldError fieldError = new FieldError("test-object-name", "test-field", "test-message")
        bindingResult.getFieldError() >> fieldError

        when:
        gatewayController.createGateway(testGateway, bindingResult)

        then: "RequestException is thrown"
        def exception = thrown(RequestException)

        and:
        exception.getMessage() == "test-message"
        exception.getHttpStatus() == HttpStatus.BAD_REQUEST
    }

    def "Should return 200 when (GET) by id is called"() {
        given: "Gateway service returns gateway"
        gatewayService.getGatewayById(testGateway.getId()) >> testGateway

        when:
        def response = gatewayController.getGatewayById(testGateway.getId())

        then:
        response.getStatusCode() == HttpStatus.OK

        and:
        response.getBody().getId() == testGateway.getId()
    }

    def "Should throw RequestException with status code 404 when (GET) by id is called with invalid id"() {
        given: "Gateway service returns gateway"
        gatewayService.getGatewayById(testGateway.getId()) >> { throw new RequestException("Gateway not found", HttpStatus.NOT_FOUND) }

        when:
        gatewayController.getGatewayById(testGateway.getId())

        then: "RequestException is thrown"
        def exception = thrown(RequestException)

        and:
        exception.getMessage() == "Gateway not found"
        exception.getHttpStatus() == HttpStatus.NOT_FOUND
    }

    def "Should throw RequestException with status code 404 when (DELETE) by id is called with invalid id"() {
        given: "Gateway service returns gateway"
        gatewayService.deleteGatewayById(testGateway.getId()) >> { throw new RequestException("Gateway not found", HttpStatus.NOT_FOUND) }

        when:
        gatewayController.deleteGatewayById(testGateway.getId())

        then: "RequestException is thrown"
        def exception = thrown(RequestException)

        and:
        exception.getMessage() == "Gateway not found"
        exception.getHttpStatus() == HttpStatus.NOT_FOUND
    }

    def "Should return 204 when DELETE by id is called"() {
        given: "Gateway service returns gateway"
        gatewayService.deleteGatewayById(testGateway.getId()) >> {}

        when:
        def response = gatewayController.deleteGatewayById(testGateway.getId())

        then:
        response.getStatusCode() == HttpStatus.NO_CONTENT
    }

    def "Should return 200 when UPDATE gateway is called"() {
        given: "Gateway service returns gateway"
        gatewayService.updateGateway(testGateway) >> testGateway

        and: "Binding result returns no errors"
        def bindingResult = Mock(BindingResult)
        bindingResult.hasErrors() >> false

        when:
        def response = gatewayController.updateGateway(testGateway, bindingResult)

        then:
        response.getStatusCode() == HttpStatus.OK
        response.getBody() == testGateway
    }

    def "Should throw RequestException with status code 400 when update gateway is called with invalid request body"() {
        given: "Gateway service returns gateway"
        gatewayService.updateGateway(testGateway) >> testGateway

        and: "Binding result returns errors"
        def bindingResult = Mock(BindingResult)
        bindingResult.hasErrors() >> true
        FieldError fieldError = new FieldError("test-object-name", "test-field", "test-message")
        bindingResult.getFieldError() >> fieldError

        when:
        gatewayController.updateGateway(testGateway, bindingResult)

        then: "RequestException is thrown"
        def exception = thrown(RequestException)

        and:
        exception.getMessage() == "test-message"
        exception.getHttpStatus() == HttpStatus.BAD_REQUEST
    }

}
