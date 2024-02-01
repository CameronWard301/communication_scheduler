package io.github.cameronward301.communication_scheduler.workflows.communication_workflow.activities

import io.fabric8.kubernetes.api.model.ConfigMapBuilder
import io.fabric8.kubernetes.client.KubernetesClient
import io.fabric8.kubernetes.client.dsl.MixedOperation
import io.fabric8.kubernetes.client.dsl.Resource
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.properties.TemporalProperties
import io.temporal.failure.ActivityFailure
import io.temporal.testing.TestActivityEnvironment
import io.temporal.testing.TestEnvironmentOptions
import spock.lang.Specification

import java.time.Duration

class GetPreferencesActivityTest extends Specification {
    TestActivityEnvironment testActivityEnvironment
    GetPreferencesActivity getPreferencesActivity
    KubernetesClient kubernetesClient

    TemporalProperties temporalProperties
    final String NAMESPACE = "default"

    //Mock kubernetes client and resources
    def mockConfigMapOperations = Mock(MixedOperation)
    def mockConfigMap = Mock(Resource)

    def setup() {
        testActivityEnvironment = TestActivityEnvironment.newInstance(
                TestEnvironmentOptions.newBuilder()
                        .setUseTimeskipping(false)
                        .build()
        )

        TemporalProperties properties = new TemporalProperties()
        properties.setNamespace(NAMESPACE)
        temporalProperties = properties


        this.kubernetesClient = Mock(KubernetesClient.class)

        testActivityEnvironment.registerActivitiesImplementations(new GetPreferencesActivityImpl(temporalProperties, kubernetesClient))
        this.getPreferencesActivity = testActivityEnvironment.newActivityStub(GetPreferencesActivity.class)
    }

    def cleanup() {
        testActivityEnvironment.close()
    }

    def "getPreferences can parse kubernetes configmap correctly"() {
        given: "Config map returned is correct"
        def configMapData = '{"startToCloseTimeout":"PT10S","maximumAttempts":100,"backoffCoefficient":2.0,"initialInterval":"PT1S","maximumInterval":"PT100S"}'
        def configMap = new ConfigMapBuilder()
                .withNewMetadata().withName("preferences").withNamespace(NAMESPACE).endMetadata()
                .addToData("GatewayTimeoutSeconds", "60")
                .addToData("RetryPolicy", configMapData).build()
        (kubernetesClient.configMaps()) >> mockConfigMapOperations
        (mockConfigMapOperations.inNamespace(NAMESPACE)) >> mockConfigMapOperations
        (mockConfigMapOperations.withName("preferences")) >> mockConfigMap
        (mockConfigMap.get()) >> configMap

        when: "getPreferences() is called"
        def preferences = getPreferencesActivity.getPreferences()

        then: "preferences are set correctly"
        preferences.backoffCoefficient == 2.0
        preferences.maximumAttempts == 100
        preferences.startToCloseTimeout == Duration.parse("PT10S")
        preferences.initialInterval == Duration.parse("PT1S")
        preferences.maximumInterval == Duration.parse("PT100S")
        preferences.gatewayTimeoutSeconds == 60
    }

    def "getPreferences should throw ApplicationFailure for invalid configMap"() {
        given: "Config map stored is invalid"
        def configMapData = "Invalid CONFIG_MAP"
        def configMap = new ConfigMapBuilder()
                .withNewMetadata().withName("preferences").endMetadata()
                .addToData("GatewayTimeoutSeconds", "60")
                .addToData("RetryPolicy", configMapData).build()
        (kubernetesClient.configMaps()) >> mockConfigMapOperations
        (mockConfigMapOperations.inNamespace(NAMESPACE)) >> mockConfigMapOperations
        (mockConfigMapOperations.withName("preferences")) >> mockConfigMap
        (mockConfigMap.get()) >> configMap

        when: "getPreferences is called"
        getPreferencesActivity.getPreferences()

        then: "should throw ApplicationFailure"
        def exception = thrown(ActivityFailure)
        exception.originalMessage == "Could not convert string to config map"
    }
}
