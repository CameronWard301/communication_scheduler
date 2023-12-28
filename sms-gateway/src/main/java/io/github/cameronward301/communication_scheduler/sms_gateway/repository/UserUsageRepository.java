package io.github.cameronward301.communication_scheduler.sms_gateway.repository;

import io.github.cameronward301.communication_scheduler.sms_gateway.model.UserUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserUsageRepository extends JpaRepository<UserUsage, String> {
    UserUsage findByUser_Id(String id);
}
