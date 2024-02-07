package io.github.cameronward301.communication_scheduler.workflows.communication_workflow.codec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.encrypt.Encryptors;

import java.util.HexFormat;

@Configuration
public class EncryptorConfig {

    @Bean
    public BytesEncryptor bytesEncryptor(
            @Value("${temporal-properties.encryption.password}") String password,
            @Value("${temporal-properties.encryption.salt}") String salt) {
        return Encryptors.stronger(password, HexFormat.of().formatHex(salt.getBytes()));
    }
}
