package com.ria.olita.tech.silingan.service.auth;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ria.olita.tech.silingan.config.OtpProperties;
import com.ria.olita.tech.silingan.exception.ValidationException;

import java.util.concurrent.TimeUnit;

@Service
public class RegistrationAuthProofService {

    private final Cache<String, RegistrationCredentials> proofCache;

    public RegistrationAuthProofService(OtpProperties otpProperties) {
        this.proofCache = Caffeine.newBuilder()
                .maximumSize(otpProperties.getCache().getMaxSize())
                .expireAfterWrite(otpProperties.getRegistrationProofMinutes(), TimeUnit.MINUTES)
                .build();
    }

    public String issueProof(String username, String password) {
        if (username == null || username.isBlank()) {
            throw new ValidationException("Username is required to issue registration auth proof");
        }
        if (password == null || password.isBlank()) {
            throw new ValidationException("Password is required to issue registration auth proof");
        }

        String proof = UUID.randomUUID().toString();
        proofCache.put(proof, new RegistrationCredentials(username, password));
        return proof;
    }

    public RegistrationCredentials consumeProof(String proof) {
        if (proof == null || proof.isBlank()) {
            return null;
        }

        RegistrationCredentials credentials = proofCache.getIfPresent(proof);
        if (credentials != null) {
            proofCache.invalidate(proof);
        }
        return credentials;
    }

    public RegistrationCredentials getProof(String proof) {
        if (proof == null || proof.isBlank()) {
            return null;
        }
        return proofCache.getIfPresent(proof);
    }

    public record RegistrationCredentials(String username, String password) {
    }
}

