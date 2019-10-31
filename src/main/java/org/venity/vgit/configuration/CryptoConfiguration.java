package org.venity.vgit.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Configuration
public class CryptoConfiguration {

    @Bean
    public ThreadLocal<MessageDigest> passwordDigest() {
        return ThreadLocal.withInitial(() -> {
            try {
                return MessageDigest.getInstance("SHA3-256");
            } catch (NoSuchAlgorithmException e) {
                return null;
            }
        });
    }
}
