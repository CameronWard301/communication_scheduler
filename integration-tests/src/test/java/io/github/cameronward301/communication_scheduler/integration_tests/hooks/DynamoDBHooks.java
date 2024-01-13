package io.github.cameronward301.communication_scheduler.integration_tests.hooks;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.github.cameronward301.communication_scheduler.integration_tests.gateway.GatewayDbModel;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

public class DynamoDBHooks {
    private final DynamoDBMapper dynamoDBMapper;
    private final GatewayDbModel gatewayDbModel;
    private final List<GatewayDbModel> gatewayDbModels;

    public DynamoDBHooks(DynamoDBMapper dynamoDBMapper,
                         @Qualifier("gatewayDbModel") GatewayDbModel gatewayDbModel,
                         @Qualifier("gatewayDbModels") List<GatewayDbModel> gatewayDbModels) {
        this.dynamoDBMapper = dynamoDBMapper;
        this.gatewayDbModel = gatewayDbModel;
        this.gatewayDbModels = gatewayDbModels;
    }

    @Before("@DynamoDbSetupEntity")
    public void setupDynamoDBEntity() {
        if (doesEntityExist(gatewayDbModel.getId())){
            removeDynamoDBEntity();
        }
        saveEntity(gatewayDbModel);
    }

    @Before("@DynamoDbAddMultipleEntities")
    public void setupDynamoDBEntities() {
        for (GatewayDbModel gateway : gatewayDbModels) {
            if (doesEntityExist(gateway.getId())){
                removeDynamoDBEntity();
            }
            saveEntity(gateway);
        }
    }

    @After("@DynamoDbRemoveEntity")
    public void removeDynamoDBEntity() {
        if (doesEntityExist(gatewayDbModel.getId())) {
            dynamoDBMapper.delete(GatewayDbModel.builder().id(gatewayDbModel.getId()).build());
        }
    }

    @After("@DynamoDbRemoveMultipleEntities")
    public void removeDynamoDBEntities() {
        for (GatewayDbModel gateway : gatewayDbModels) {
            if (doesEntityExist(gateway.getId())) {
                dynamoDBMapper.delete(GatewayDbModel.builder().id(gateway.getId()).build());
            }
        }
    }

    private boolean doesEntityExist(String id) {
        return dynamoDBMapper.load(GatewayDbModel.class, id) != null;
    }

    private void saveEntity(GatewayDbModel gateway) {
        dynamoDBMapper.save(gateway);
    }
}
