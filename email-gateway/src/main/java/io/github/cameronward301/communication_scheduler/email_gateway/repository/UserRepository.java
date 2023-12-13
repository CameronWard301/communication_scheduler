package io.github.cameronward301.communication_scheduler.email_gateway.repository;

import io.github.cameronward301.communication_scheduler.email_gateway.model.EmailUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<EmailUser, String> {
}
