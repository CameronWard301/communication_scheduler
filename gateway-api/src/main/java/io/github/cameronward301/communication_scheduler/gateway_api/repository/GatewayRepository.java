package io.github.cameronward301.communication_scheduler.gateway_api.repository;

import io.github.cameronward301.communication_scheduler.gateway_api.model.Gateway;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GatewayRepository extends MongoRepository<Gateway, String>{
    Page<Gateway> findAll (Pageable pageable);

    Page<Gateway> findByFriendlyNameRegexAndEndpointUrlRegexAndDescriptionRegex(String friendlyName, String dateCreated, String description, Pageable pageable);

}
