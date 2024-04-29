package io.github.cameronward301.communication_scheduler.email_gateway.component;

import io.github.cameronward301.communication_scheduler.email_gateway.model.EmailUser;
import io.github.cameronward301.communication_scheduler.email_gateway.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for loading the users into the database.
 * It simulates a business that already has customers in their internal database
 */
@Component
public class UserLoader implements CommandLineRunner {

    private final UserRepository userRepository;

    @Value("${user1.email}")
    private String user1_email;

    @Value("${user2.email}")
    private String user2_email;

    public UserLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        EmailUser emailUser1 = EmailUser.builder()
                .id("62f8a8e1-f55a-4d9a-ab15-852168a321a4")
                .firstName("Cameron")
                .email(user1_email)
                .build();

        EmailUser emailUser2 = EmailUser.builder()
                .id("f882fa87-c249-4c14-bb61-78542217f79d")
                .firstName("Cameron")
                .email(user2_email)
                .build();

        userRepository.save(emailUser1);
        userRepository.save(emailUser2);
    }
}
