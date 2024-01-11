package io.github.cameronward301.communication_scheduler.gateway_api.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class for a Gateway
 * Primary key is a composite key of the gateway id and the date_created fields.
 */
@Data
@Builder
@DynamoDBTable(tableName = "") //Defined in the application.properties file and configured in DynamoDbConfiguration.java
@AllArgsConstructor
@NoArgsConstructor
public class Gateway {
    private String id;

    @DynamoDBAttribute(attributeName = "endpoint_url")
    @NotBlank(message = "'endpointUrl' cannot be empty")
    private String endpointUrl;

    @DynamoDBAttribute(attributeName = "friendly_name")
    @NotBlank(message = "'friendlyName' cannot be empty")
    private String friendlyName;
    private String description = "";

    @DynamoDBAttribute(attributeName = "date_created")
    private String dateCreated;

    @DynamoDBHashKey(attributeName = "id")
    public String getId() {
        return id;
    }

    @DynamoDBRangeKey(attributeName = "date_created")
    public String getDateCreated() {
        return dateCreated;
    }
}
