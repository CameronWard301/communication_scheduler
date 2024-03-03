package io.github.cameronward301.communication_scheduler.gateway_api.repository;

import io.github.cameronward301.communication_scheduler.gateway_api.model.Gateway;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface GatewayRepository extends MongoRepository<Gateway, String> {
    Page<Gateway> findAll(Pageable pageable);

    @Query("{'friendlyName': {$regex: ?0, $options: 'i'}, 'endpointUrl': {$regex: ?1, $options: 'i'}, 'description': {$regex: ?2, $options: 'i'}}")
    Page<Gateway> findByFriendlyNameRegexAndEndpointUrlRegexAndDescriptionRegexIgnoreCase(String friendlyName, String endpointUrl, String description, Pageable pageable);


}
