package io.github.cameronward301.communication_scheduler.worker.communication_worker.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.context.annotation.Bean
import reactor.netty.http.client.HttpClient
import reactor.netty.tcp.SslProvider
import spock.lang.Shared
import spock.lang.Specification

import javax.net.ssl.SSLException
import java.util.function.Consumer

class WebClientConfigTest extends Specification {



    @Shared
    ApplicationContextRunner context = new ApplicationContextRunner()
            .withUserConfiguration(WebClientConfig, SslContextWrapperImpl)

    def setup() {
        System.clearProperty("web-client.ssl-verification")
    }

    def "WebClient is created creating ssl version"() {


        given:
        System.setProperty("web-client.ssl-verification", "true")


        when:
        context.run {context->
            context.containsBean("secureWebclient")
            context.getBean("secureWebclient") != null
        }

        then:
        noExceptionThrown()
    }

    def "WebClient is created creating non ssl version"() {


        given:
        System.setProperty("web-client.ssl-verification", "false")


        when:
        context.run {context->
            context.containsBean("insecureWebClient")
            context.getBean("insecureWebClient") != null
        }

        then:
        noExceptionThrown()
    }

    def "Exception is thrown when creating insecure WebClient"() {
        given:
        SslContextWrapper SslContextWrapper = Mock(SslContextWrapper)
        SslContextWrapper.buildSslContext() >> { throw new SSLException("test") }
        WebClientConfig webClientConfig = new WebClientConfig(SslContextWrapper)

        when:
        webClientConfig.createSslContext()

        then:
        def exception = thrown(RuntimeException)
        exception.getMessage() == "javax.net.ssl.SSLException: test"
    }
}
