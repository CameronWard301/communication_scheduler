package io.github.cameronward301.communication_scheduler.preferences_api.service

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.core.type.TypeReference;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder
import io.fabric8.kubernetes.client.KubernetesClient
import io.fabric8.kubernetes.client.server.mock.KubernetesMockServer
import io.github.cameronward301.communication_scheduler.preferences_api.exception.RequestException
import io.github.cameronward301.communication_scheduler.preferences_api.model.GatewayTimeout
import io.github.cameronward301.communication_scheduler.preferences_api.model.RetryPolicy
import io.github.cameronward301.communication_scheduler.preferences_api.properties.ClusterPreferences
import org.springframework.http.HttpStatus
import spock.lang.Specification


class PreferencesServiceTest extends Specification {
    PreferencesService preferencesService
    private KubernetesMockServer mockServer = new KubernetesMockServer()
    private KubernetesClient kubernetesClient
    def objectMapper = new ObjectMapper()
    def clusterPreferences = new ClusterPreferences()


    def setup() {
        clusterPreferences.setNamespace("default")
        mockServer.init()
        kubernetesClient = mockServer.createClient()
        preferencesService = new PreferencesService(kubernetesClient, clusterPreferences, objectMapper)
    }

    def "Should retrieve preferences from the cluster"() {
        given: "Preferences configmap"
        def configMapData = '{"startToCloseTimeout":"PT10S","maximumAttempts":100,"backoffCoefficient":2.0,"initialInterval":"PT1S","maximumInterval":"PT100S"}'
        def configMap = new ConfigMapBuilder()
                .withNewMetadata().withName("preferences").withNamespace("default").endMetadata()
                .addToData("GatewayTimeoutSeconds", "60")
                .addToData("RetryPolicy", configMapData).build()

        and: "Server is configured to return the configmap"
        mockServer.expect().get()
                .withPath("/api/v1/namespaces/default/configmaps/preferences")
                .andReturn(200,
                        configMap)
                .always()

        when: "service is called"
        def response = preferencesService.getPreferences()

        then:
        response != null

        and:
        response.getGatewayTimeoutSeconds() == 60
        response.getRetryPolicy().getBackoffCoefficient() == 2.0
        response.getRetryPolicy().getStartToCloseTimeout() == "PT10S"
        response.getRetryPolicy().getMaximumAttempts() == 100
        response.getRetryPolicy().getInitialInterval() == "PT1S"
        response.getRetryPolicy().getMaximumInterval() == "PT100S"
    }

    def "Should throw internal server error if object mapper can't process json when getting configmap"() {
        given: "Preferences configmap"
        def configMapData = '{"startToCloseTimeout":"PT10S","maximumAttempts":100,"backoffCoefficient":2.0,"initialInterval":"PT1S","maximumInterval":"PT100S"}'
        def configMap = new ConfigMapBuilder()
                .withNewMetadata().withName("preferences").withNamespace("default").endMetadata()
                .addToData("GatewayTimeoutSeconds", "60")
                .addToData("RetryPolicy", configMapData).build()

        and: "server is configured to return the configmap"
        mockServer.expect().get()
                .withPath("/api/v1/namespaces/default/configmaps/preferences")
                .andReturn(200,
                        configMap)
                .always()


        and: "Service is using a mocked ObjectMapper"
        def mapper = Mock(ObjectMapper)
        def service = new PreferencesService(kubernetesClient, clusterPreferences, mapper)

        and: "Object mapper throws json processing exception"
        mapper.readValue(_ as String, new TypeReference<Map<String, String>>() {}) >> {
            throw new JsonProcessingException("test-exception") {}
        }


        when:
        service.getPreferences()

        then: "RequestException is thrown"
        def exception = thrown(RequestException)

        and:
        exception.getMessage() == "Could not process preferences config map"
        exception.getHttpStatus() == HttpStatus.INTERNAL_SERVER_ERROR
    }

    def "Should save retry policy to the cluster"() {
        given: "Retry policy"
        def retryPolicy = RetryPolicy.builder()
                .startToCloseTimeout("test-start-to-close")
                .maximumAttempts(1234)
                .backoffCoefficient(3.0)
                .initialInterval("test-initial")
                .maximumInterval("maximum-interval")
                .build()

        def configMap = new ConfigMapBuilder()
                .withNewMetadata().withName("preferences").withNamespace("default").endMetadata()
                .addToData("GatewayTimeoutSeconds", "60")
                .addToData("RetryPolicy", objectMapper.writeValueAsString(retryPolicy)).build()

        and: "Server is configured to return the configmap"
        mockServer.expect().get()
                .withPath("/api/v1/namespaces/default/configmaps/preferences")
                .andReturn(200,
                        configMap)
                .always()
        mockServer.expect().patch()
                .withPath("/api/v1/namespaces/default/configmaps/preferences")
                .andReturn(200,
                        configMap)
                .always()


        when: "service is called"
        def response = preferencesService.setRetryPolicy(retryPolicy)

        then:
        response != null

        and:
        response == retryPolicy
    }

    def "Should throw internal server error if object mapper can't write object to string when saving the retry-policy"() {
        given: "Retry policy"
        def retryPolicy = RetryPolicy.builder()
                .startToCloseTimeout("test-start-to-close")
                .maximumAttempts(1234)
                .backoffCoefficient(3.0)
                .initialInterval("test-initial")
                .maximumInterval("maximum-interval")
                .build()

        def configMap = new ConfigMapBuilder()
                .withNewMetadata().withName("preferences").withNamespace("default").endMetadata()
                .addToData("GatewayTimeoutSeconds", "60")
                .addToData("RetryPolicy", objectMapper.writeValueAsString(retryPolicy)).build()

        and: "Server is configured to return the configmap"
        mockServer.expect().get()
                .withPath("/api/v1/namespaces/default/configmaps/preferences")
                .andReturn(200,
                        configMap)
                .always()
        mockServer.expect().patch()
                .withPath("/api/v1/namespaces/default/configmaps/preferences")
                .andReturn(200,
                        configMap)
                .always()


        and: "Service is using a mocked ObjectMapper"
        def mapper = Mock(ObjectMapper)
        def service = new PreferencesService(kubernetesClient, clusterPreferences, mapper)

        and: "Object mapper throws json processing exception"
        mapper.writeValueAsString(_ as RetryPolicy) >> {
            throw new JsonProcessingException("test-exception") {}
        }


        when:
        service.setRetryPolicy(retryPolicy)

        then: "RequestException is thrown"
        def exception = thrown(RequestException)

        and:
        exception.getMessage() == "Could not save retry policy, INVALID JSON"
        exception.getHttpStatus() == HttpStatus.BAD_REQUEST
    }

    def "Should throw internal server error if object mapper can't read value from string when saving the retry-policy"() {
        given: "Retry policy"
        def retryPolicy = RetryPolicy.builder()
                .startToCloseTimeout("test-start-to-close")
                .maximumAttempts(1234)
                .backoffCoefficient(3.0)
                .initialInterval("test-initial")
                .maximumInterval("maximum-interval")
                .build()

        def configMap = new ConfigMapBuilder()
                .withNewMetadata().withName("preferences").withNamespace("default").endMetadata()
                .addToData("GatewayTimeoutSeconds", "60")
                .addToData("RetryPolicy", objectMapper.writeValueAsString(retryPolicy)).build()

        and: "Server is configured to return the configmap"
        mockServer.expect().get()
                .withPath("/api/v1/namespaces/default/configmaps/preferences")
                .andReturn(200,
                        configMap)
                .always()
        mockServer.expect().patch()
                .withPath("/api/v1/namespaces/default/configmaps/preferences")
                .andReturn(200,
                        configMap)
                .always()


        and: "Service is using a mocked ObjectMapper"
        def mapper = Mock(ObjectMapper)
        def service = new PreferencesService(kubernetesClient, clusterPreferences, mapper)

        and: "Object mapper throws json processing exception"
        mapper.readValue(_ as String, RetryPolicy.class) >> {
            throw new JsonProcessingException("test-exception") {}
        }


        when:
        service.setRetryPolicy(retryPolicy)

        then: "RequestException is thrown"
        def exception = thrown(RequestException)

        and:
        exception.getMessage() == "Could not convert policy string to object, INVALID JSON"
        exception.getHttpStatus() == HttpStatus.INTERNAL_SERVER_ERROR
    }

    def "Should save gateway-timeout to the cluster"() {
        given: "Gateway timeout"
        def gatewayTimeout = GatewayTimeout.builder().gatewayTimeoutSeconds(100).build()

        and: "Retry Policy"
        def retryPolicy = RetryPolicy.builder()
                .startToCloseTimeout("test-start-to-close")
                .maximumAttempts(1234)
                .backoffCoefficient(3.0)
                .initialInterval("test-initial")
                .maximumInterval("maximum-interval")
                .build()

        def configMap = new ConfigMapBuilder()
                .withNewMetadata().withName("preferences").withNamespace("default").endMetadata()
                .addToData("GatewayTimeoutSeconds", String.valueOf(gatewayTimeout.getGatewayTimeoutSeconds()))
                .addToData("RetryPolicy", objectMapper.writeValueAsString(retryPolicy)).build()

        and: "Server is configured to return the configmap"
        mockServer.expect().get()
                .withPath("/api/v1/namespaces/default/configmaps/preferences")
                .andReturn(200,
                        configMap)
                .always()
        mockServer.expect().patch()
                .withPath("/api/v1/namespaces/default/configmaps/preferences")
                .andReturn(200,
                        configMap)
                .always()


        when: "service is called"
        def response = preferencesService.setGatewayTimeoutSeconds(gatewayTimeout)

        then:
        response != null

        and:
        response == gatewayTimeout
    }

    def "Should throw RequestException if cannot read the string to object"() {
        given: "Gateway timeout"
        def gatewayTimeout = GatewayTimeout.builder().gatewayTimeoutSeconds(100).build()

        and: "Retry Policy"
        def retryPolicy = RetryPolicy.builder()
                .startToCloseTimeout("test-start-to-close")
                .maximumAttempts(1234)
                .backoffCoefficient(3.0)
                .initialInterval("test-initial")
                .maximumInterval("maximum-interval")
                .build()

        def configMap = new ConfigMapBuilder()
                .withNewMetadata().withName("preferences").withNamespace("default").endMetadata()
                .addToData("GatewayTimeoutSeconds", String.valueOf(gatewayTimeout.getGatewayTimeoutSeconds()))
                .addToData("RetryPolicy", objectMapper.writeValueAsString(retryPolicy)).build()

        and: "Server is configured to return the configmap"
        mockServer.expect().get()
                .withPath("/api/v1/namespaces/default/configmaps/preferences")
                .andReturn(200,
                        configMap)
                .always()
        mockServer.expect().patch()
                .withPath("/api/v1/namespaces/default/configmaps/preferences")
                .andReturn(200,
                        configMap)
                .always()

        and: "Service is using a mocked ObjectMapper"
        def mapper = Mock(ObjectMapper)
        def service = new PreferencesService(kubernetesClient, clusterPreferences, mapper)

        and: "Object mapper throws json processing exception"
        mapper.readValue(_ as String, GatewayTimeout.class) >> {
            throw new JsonProcessingException("test-exception") {}
        }


        when: "service is called"
        service.setGatewayTimeoutSeconds(gatewayTimeout)

        then: "RequestException is thrown"
        def exception = thrown(RequestException)

        and:
        exception.getMessage() == "Could not convert policy string to object, INVALID JSON"
        exception.getHttpStatus() == HttpStatus.INTERNAL_SERVER_ERROR
    }
}
