package io.github.cameronward301.communication_scheduler.sms_gateway.repository;

import io.github.cameronward301.communication_scheduler.sms_gateway.model.SmsUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<SmsUser, String> {
}
