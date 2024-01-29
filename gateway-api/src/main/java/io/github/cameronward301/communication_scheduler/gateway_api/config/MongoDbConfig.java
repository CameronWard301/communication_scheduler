// Adapted from here https://www.baeldung.com/spring-data-mongodb-tutorial#2-java-configuration
package io.github.cameronward301.communication_scheduler.gateway_api.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@Configuration
public class MongoDbConfig extends AbstractMongoClientConfiguration {
    @Value("${gateway.mongodb.database.name}")
    private String dbName = "";
    @Value("${gateway.mongodb.connection.string}")
    private String connectionString = "";

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
}
