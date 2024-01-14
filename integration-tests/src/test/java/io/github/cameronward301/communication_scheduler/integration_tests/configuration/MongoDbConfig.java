package io.github.cameronward301.communication_scheduler.integration_tests.configuration;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.github.cameronward301.communication_scheduler.integration_tests.gateway.Gateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MongoDbConfig extends AbstractMongoClientConfiguration {
    @Value("${gateway-api.mongodb.database.name}")
    private String dbName = "";
    @Value("${gateway-api.mongodb.connection.string}")
    private String connectionString = "";
    @Value("${gateway-api.entity.id}")
    private String testGatewayId;

    @Override
    protected String getDatabaseName() {
        return dbName;
    }

    @Override
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString(this.connectionString);
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        return MongoClients.create(mongoClientSettings);
    }

    /**
     * Creates a Gateway bean for use in tests
     *
     * @return Gateway bean
     */
    @Bean
    public Gateway gatewayDbModel() {
        return Gateway.builder()
                .id(testGatewayId)
                .dateCreated("2021-01-01T00:00:00.000Z")
                .endpointUrl("https://test-gateway.com/monthly/newsletter")
                .description("test gateway description")
                .friendlyName("test gateway name")
                .build();
    }

    @Bean
    public List<Gateway> gatewayDbModels() {
        List<Gateway> gateways = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            gateways.add(Gateway.builder()
                    .id(testGatewayId + "-" + (i + 1))
                    .dateCreated("2021-01-01T00:00:00.000Z")
                    .endpointUrl("https://test-gateway-" + (i + 1) + ".com/monthly/newsletter")
                    .description("test gateway " + (i + 1) + " description")
                    .friendlyName("test gateway " + (i + 1) + " name")
                    .build());
        }
        return gateways;
    }
}
