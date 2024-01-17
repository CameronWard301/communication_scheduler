package io.github.cameronward301.communication_scheduler.worker.communication_worker.repository;

import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.repository.GatewayRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkerGatewayRepository extends GatewayRepository {
}
