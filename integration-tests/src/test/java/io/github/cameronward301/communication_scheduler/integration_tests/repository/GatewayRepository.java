package io.github.cameronward301.communication_scheduler.integration_tests.repository;

import io.github.cameronward301.communication_scheduler.integration_tests.gateway.Gateway;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GatewayRepository extends MongoRepository<Gateway, String> {
}
