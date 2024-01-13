package io.github.cameronward301.communication_scheduler.integration_tests.gateway;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.*;

@Getter
@Setter
@Builder
@DynamoDBTable(tableName = "") //Defined in the application.properties file and configured in DynamoDbConfiguration.java
@AllArgsConstructor
@NoArgsConstructor
public class GatewayDbModel {
    private String id;

    @DynamoDBAttribute(attributeName = "endpoint_url")
    private String endpointUrl;

    @DynamoDBAttribute(attributeName = "friendly_name")
    private String friendlyName;
    private String description = "";

    @DynamoDBAttribute(attributeName = "date_created")
    private String dateCreated;

    @DynamoDBHashKey(attributeName = "id")
    public String getId() {
        return id;
    }
}
