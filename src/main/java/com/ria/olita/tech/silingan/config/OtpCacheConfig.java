package com.ria.olita.tech.silingan.config;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ria.olita.tech.silingan.service.otp.OtpData;

import lombok.RequiredArgsConstructor;

/**
 * Configuration class for Caffeine Cache.
 * Provides dedicated caches for OTP storage and cooldown tracking.
 */
@Configuration
@EnableCaching
@RequiredArgsConstructor
public class OtpCacheConfig {

    private final OtpProperties otpProperties;

    /**
     * Cache for storing OTP data.
     * Key: Mobile number (String)
     * Value: OtpData (contains hashed OTP and attempt count)
     * Expiration: Configurable via otp.expiration-minutes (default 5 minutes)
     */
    @Bean
    public Cache<String, OtpData> otpCache() {
        return Caffeine.newBuilder()
                .maximumSize(otpProperties.getCache().getMaxSize())
                .expireAfterWrite(otpProperties.getExpirationMinutes(), TimeUnit.MINUTES)
                .recordStats()
                .build();
    }

    /**
     * Cache for tracking OTP request cooldowns.
     * Key: Mobile number (String)
     * Value: Timestamp of last OTP request (Long)
     * Expiration: Configurable via otp.cooldown-seconds (default 60 seconds)
     */
    @Bean
    public Cache<String, Long> cooldownCache() {
        return Caffeine.newBuilder()
                .maximumSize(otpProperties.getCache().getMaxSize())
                .expireAfterWrite(otpProperties.getCooldownSeconds(), TimeUnit.SECONDS)
                .recordStats()
                .build();
    }

    /**
     * Cache for short-lived OTP verification proof used by self-service registration.
     * Key: normalized mobile number (String)
     * Value: verification marker (Boolean)
     */
    @Bean
    public Cache<String, Boolean> registrationOtpProofCache() {
        return Caffeine.newBuilder()
                .maximumSize(otpProperties.getCache().getMaxSize())
                .expireAfterWrite(otpProperties.getRegistrationProofMinutes(), TimeUnit.MINUTES)
                .recordStats()
                .build();
    }

    /**
     * Primary cache manager for Spring Cache abstraction.
     */
    @Bean
    @Primary
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(otpProperties.getCache().getMaxSize())
                .expireAfterWrite(otpProperties.getExpirationMinutes(), TimeUnit.MINUTES)
                .recordStats());
        return cacheManager;
    }
}

