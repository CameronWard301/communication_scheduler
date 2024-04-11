package io.github.cameronward301.communication_scheduler.preferences_api.controller

import io.github.cameronward301.communication_scheduler.preferences_api.exception.RequestException
import io.github.cameronward301.communication_scheduler.preferences_api.model.GatewayTimeout
import io.github.cameronward301.communication_scheduler.preferences_api.model.Preferences
import io.github.cameronward301.communication_scheduler.preferences_api.model.RetryPolicy
import io.github.cameronward301.communication_scheduler.preferences_api.service.PreferencesService
import org.springframework.http.HttpStatus
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import spock.lang.Specification

class PreferencesControllerTest extends Specification {

    PreferencesController preferencesController
    def preferencesService = Mock(PreferencesService)
    def retryPolicy = RetryPolicy.builder()
            .backoffCoefficient(2.0)
            .initialInterval("PT30S")
            .maximumAttempts(1234)
            .maximumInterval("PT10S")
            .startToCloseTimeout("PT20S")
            .build()
    def preferences = Preferences.builder()
    .gatewayTimeoutSeconds(10)
    .retryPolicy(retryPolicy).build()

    def setup(){
        preferencesController = new PreferencesController(preferencesService)
    }

    def "Should return 200 when getting preferences"(){
        given: "service returns preferences"
        preferencesService.getPreferences() >> preferences

        when: "calling GET preferences"
        def response = preferencesController.getPreferences()

        then:
        response.getStatusCode() == HttpStatus.OK

        and:
        response.getBody() == preferences
    }

    def "Should return 200 when setting retry policy preferences"(){
        given: "service returns preferences"
        preferencesService.setRetryPolicy(retryPolicy) >> retryPolicy

        and: "Binding result has no errors"
        def bindingResult = Mock(BindingResult)
        bindingResult.hasErrors() >> false

        when: "calling GET preferences"
        def response = preferencesController.setRetryPolicy(retryPolicy, bindingResult)

        then:
        response.getStatusCode() == HttpStatus.OK

        and:
        response.getBody() == retryPolicy
    }

    def "Should return 400 when binding result has errors when setting retry-policy"(){
        given: "Binding result has errors"
        def bindingResult = Mock(BindingResult)
        bindingResult.hasErrors() >> true
        FieldError fieldError = new FieldError("test-object-name", "test-field", "test-message")
        bindingResult.getFieldError() >> fieldError

        when: "Calling PUT retry-policy"
        preferencesController.setRetryPolicy(retryPolicy, bindingResult)

        then: "RequestException is thrown"
        def exception = thrown(RequestException)

        and:
        exception.getMessage() == "test-message"
        exception.getHttpStatus() == HttpStatus.BAD_REQUEST
    }

    def "Should return 400 when initial interval is invalid"() {
        given: "Invalid initial interval"
        def retryPolicy = RetryPolicy.builder()
                .backoffCoefficient(2.0)
                .initialInterval("invalid-test")
                .maximumAttempts(1234)
                .maximumInterval("PT10S")
                .startToCloseTimeout("PT20S")
                .build()

        and: "Binding result has no errors"
        def bindingResult = Mock(BindingResult)
        bindingResult.hasErrors() >> false

        when: "Calling PUT retry-policy"
        preferencesController.setRetryPolicy(retryPolicy, bindingResult)

        then: "RequestException is thrown"
        def exception = thrown(RequestException)

        and:
        exception.getMessage() == "Could not parse 'initialInterval' to a datetime object"
        exception.getHttpStatus() == HttpStatus.BAD_REQUEST
    }

    def "Should return 400 when maximum interval is invalid"() {
        given: "Invalid initial interval"
        def retryPolicy = RetryPolicy.builder()
                .backoffCoefficient(2.0)
                .initialInterval("PT30S")
                .maximumAttempts(1234)
                .maximumInterval("invalid-test")
                .startToCloseTimeout("PT20S")
                .build()

        and: "Binding result has no errors"
        def bindingResult = Mock(BindingResult)
        bindingResult.hasErrors() >> false

        when: "Calling PUT retry-policy"
        preferencesController.setRetryPolicy(retryPolicy, bindingResult)

        then: "RequestException is thrown"
        def exception = thrown(RequestException)

        and:
        exception.getMessage() == "Could not parse 'maximumInterval' to a datetime object"
        exception.getHttpStatus() == HttpStatus.BAD_REQUEST
    }

    def "Should return 400 when start to close timeout is invalid"() {
        given: "Invalid initial interval"
        def retryPolicy = RetryPolicy.builder()
                .backoffCoefficient(2.0)
                .initialInterval("PT30S")
                .maximumAttempts(1234)
                .maximumInterval("PT10S")
                .startToCloseTimeout("invalid-test")
                .build()

        and: "Binding result has no errors"
        def bindingResult = Mock(BindingResult)
        bindingResult.hasErrors() >> false

        when: "Calling PUT retry-policy"
        preferencesController.setRetryPolicy(retryPolicy, bindingResult)

        then: "RequestException is thrown"
        def exception = thrown(RequestException)

        and:
        exception.getMessage() == "Could not parse 'startToCloseTimeout' to a datetime object"
        exception.getHttpStatus() == HttpStatus.BAD_REQUEST
    }

    def "Should return 200 when setting gateway timeout setting"(){
        given: "GatewayTimeout"
        def gatewayTimeout = GatewayTimeout.builder().gatewayTimeoutSeconds(60).build()

        and: "service returns preferences"
        preferencesService.setGatewayTimeoutSeconds(gatewayTimeout) >> gatewayTimeout

        and: "Binding result has no errors"
        def bindingResult = Mock(BindingResult)
        bindingResult.hasErrors() >> false

        when: "calling PUT gateway timeout"
        def response = preferencesController.setGatewayTimeout(gatewayTimeout, bindingResult)

        then:
        response.getStatusCode() == HttpStatus.OK

        and:
        response.getBody() == gatewayTimeout
    }

    def "Should return 400 when binding result has errors when setting gateway timeout"(){
        given: "GatewayTimeout"
        def gatewayTimeout = GatewayTimeout.builder().gatewayTimeoutSeconds(60).build()

        and: "Binding result has errors"
        def bindingResult = Mock(BindingResult)
        bindingResult.hasErrors() >> true
        FieldError fieldError = new FieldError("test-object-name", "test-field", "test-message")
        bindingResult.getFieldError() >> fieldError

        when: "Calling GET preferences"
        preferencesController.setGatewayTimeout(gatewayTimeout, bindingResult)

        then: "RequestException is thrown"
        def exception = thrown(RequestException)

        and:
        exception.getMessage() == "test-message"
        exception.getHttpStatus() == HttpStatus.BAD_REQUEST
    }


}
