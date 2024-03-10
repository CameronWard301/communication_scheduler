package io.github.cameronward301.communication_scheduler.integration_tests.hooks;


import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.github.cameronward301.communication_scheduler.integration_tests.gateway.Gateway;
import io.github.cameronward301.communication_scheduler.integration_tests.repository.GatewayRepository;
import io.github.cameronward301.communication_scheduler.integration_tests.world.World;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

public class MongoDbHooks {

    private final GatewayRepository gatewayRepository;
    private final Gateway gateway;
    private final List<Gateway> gateways;
    private final World world;

    public MongoDbHooks(GatewayRepository gatewayRepository,
                        @Qualifier("gatewayDbModel") Gateway gateway,
                        @Qualifier("gatewayDbModels") List<Gateway> gateways,
                        World world) {
        this.gatewayRepository = gatewayRepository;
        this.gateways = gateways;
        this.gateway = gateway;
        this.world = world;
    }

    @Before("@MongoDbSetupEntity")
    public void setupMongoDBEntity() {
        if (doesEntityExist(gateway.getId())) {
            removeMongoDBEntity();
        }
        saveEntity(gateway);
    }

    @Before("@MongoDbAddMultipleEntities")
    public void setupMongoDBEntities() {
        for (Gateway gateway : gateways) {
            if (doesEntityExist(gateway.getId())) {
                removeMongoDBEntity();
            }
            saveEntity(gateway);
        }
    }

    @After("@MongoDbRemoveEntity")
    public void removeMongoDBEntity() {
        if (doesEntityExist(gateway.getId())) {
            gatewayRepository.deleteById(gateway.getId());
        }
    }

    @After("MongoDbRemoveEntityFromWorld")
    public void removeMongoDBEntityFromWorld() {
        if (doesEntityExist(world.getGateway().getId())) {
            gatewayRepository.deleteById(world.getGateway().getId());
        }
    }

    @After("@MongoDbRemoveMultipleEntities")
    public void removeMongoDBEntities() {
        for (Gateway gateway : gateways) {
            if (doesEntityExist(gateway.getId())) {
                gatewayRepository.deleteById(gateway.getId());
            }
        }
    }

    private boolean doesEntityExist(String id) {
        return gatewayRepository.existsById(id);
    }

    private void saveEntity(Gateway gateway) {
        Gateway model = gatewayRepository.save(gateway);
    }
}
