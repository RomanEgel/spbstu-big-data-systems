package com.example.lab3client

import org.apache.http.conn.ssl.NoopHostnameVerifier
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.conn.ssl.TrustSelfSignedStrategy
import org.apache.http.impl.client.HttpClients
import org.apache.http.ssl.SSLContextBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ClassPathResource
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import java.security.KeyStore

@SpringBootApplication
class ClientApp {
    @Bean
    fun restTemplate(@Value("\${server.ssl.key-store-password}") keystorePassword: String): RestTemplate {
        val restTemplate = RestTemplate()
        val keyStore = KeyStore.getInstance("jks")
        keyStore.load(ClassPathResource("gateway.jks").inputStream, keystorePassword.toCharArray())

        val socketFactory = SSLConnectionSocketFactory(SSLContextBuilder()
                .loadTrustMaterial(null, TrustSelfSignedStrategy())
                .loadKeyMaterial(keyStore, keystorePassword.toCharArray()).build(),
                NoopHostnameVerifier.INSTANCE)

        val httpClient = HttpClients.custom()
                .setSSLSocketFactory(socketFactory)
                .setMaxConnTotal(5)
                .setMaxConnPerRoute(5)
                .build()
        val requestFactory = HttpComponentsClientHttpRequestFactory(httpClient)
        requestFactory.setReadTimeout(10000)
        requestFactory.setConnectTimeout(10000)

        restTemplate.requestFactory = requestFactory
        return restTemplate
    }
}

fun main(args: Array<String>) {
    runApplication<ClientApp>(*args)
}

@RestController
@RequestMapping("/gateway")
class GatewayController(val restTemplate: RestTemplate, @Value("\${endpoint.server}") val serverUrl: String) {
    val logger: Logger = LoggerFactory.getLogger(GatewayController::class.java)

    @GetMapping("/data")
    fun getData(): String {
        logger.info("Returning data from gateway")
        return "Hello from gateway"
    }

    @GetMapping("server-data")
    fun getServerData(): String? {
        logger.info("Trying to get data from server '$serverUrl'")

        return restTemplate.getForObject(serverUrl, String::class.java)
    }

}