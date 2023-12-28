package io.github.cameronward301.communication_scheduler.sms_gateway.component;

import io.github.cameronward301.communication_scheduler.sms_gateway.model.SmsUser;
import io.github.cameronward301.communication_scheduler.sms_gateway.model.UserUsage;
import io.github.cameronward301.communication_scheduler.sms_gateway.repository.UserRepository;
import io.github.cameronward301.communication_scheduler.sms_gateway.repository.UserUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class UserAndUsageLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserUsageRepository userUsageRepository;

    @Value("${user1.phoneNumber}")
    private String user1_phoneNumber;

    @Value("${user1.name}")
    private String user1_name;



    @Value("${user2.phoneNumber}")
    private String user2_phoneNumber;

    @Value("${user2.name}")
    private String user2_name;

    @Override
    public void run(String... args) {
        SmsUser smsUser1 = SmsUser.builder()
                .id("c96f0fdd-0029-4b3a-91e0-b93f2d93713d")
                .firstName(user1_name)
                .phoneNumber("+" + user1_phoneNumber)
                .build();

        SmsUser smsUser2 = SmsUser.builder()
                .id("d1bb525d-5732-4396-b4de-c64baec8e3b4")
                .firstName(user2_name)
                .phoneNumber("+" + user2_phoneNumber)
                .build();

        smsUser1 = userRepository.save(smsUser1);
        smsUser2 = userRepository.save(smsUser2);

        UserUsage userUsage1 = UserUsage.builder()
                .id("00dff09f-65b3-40f3-90a2-1c565e5a3c34")
                .malwareBlocked(ThreadLocalRandom.current().nextInt(0, 10))
                .advertsBlocked(ThreadLocalRandom.current().nextInt(10, 1000))
                .sitesVisited(ThreadLocalRandom.current().nextInt(0, 5000))
                .user(smsUser1)
                .build();

        UserUsage userUsage2 = UserUsage.builder()
                .id("f8f9ef40-7707-4c9c-8f87-ef9270c31012")
                .malwareBlocked(ThreadLocalRandom.current().nextInt(0, 10))
                .advertsBlocked(ThreadLocalRandom.current().nextInt(10, 1000))
                .sitesVisited(ThreadLocalRandom.current().nextInt(0, 5000))
                .user(smsUser2)
                .build();

        userUsageRepository.save(userUsage1);
        userUsageRepository.save(userUsage2);

    }
}
