package io.github.cameronward301.communication_scheduler.workflows.communication_workflow.repository;

import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.model.Gateway;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GatewayRepository extends MongoRepository<Gateway, String> {
}
