package io.github.cameronward301.communication_scheduler.email_gateway.component;

import io.github.cameronward301.communication_scheduler.email_gateway.model.EmailContent;
import io.github.cameronward301.communication_scheduler.email_gateway.repository.ContentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class ContentLoader implements CommandLineRunner {

    private final ContentRepository contentRepository;

    public ContentLoader(ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    @Override
    public void run(String... args) {
        EmailContent emailContent1 = EmailContent.builder()
                .id("d5130460-f28f-4ee3-823f-3a13c802846a")
                .userId("62f8a8e1-f55a-4d9a-ab15-852168a321a4")
                .malware(ThreadLocalRandom.current().nextInt(0, 30))
                .adverts(ThreadLocalRandom.current().nextInt(100, 5000))
                .sites(ThreadLocalRandom.current().nextInt(0, 10000))
                .build();

        EmailContent emailContent2 = EmailContent.builder()
                .id("d8db60ac-a554-4737-b8e1-86b439bd01e3")
                .userId("f882fa87-c249-4c14-bb61-78542217f79d")
                .malware(ThreadLocalRandom.current().nextInt(0, 30))
                .adverts(ThreadLocalRandom.current().nextInt(100, 5000))
                .sites(ThreadLocalRandom.current().nextInt(0, 10000))
                .build();

        contentRepository.save(emailContent1);
        contentRepository.save(emailContent2);
    }
}
