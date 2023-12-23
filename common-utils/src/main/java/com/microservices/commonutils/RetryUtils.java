package com.microservices.commonutils;

import com.microservices.appconfigdata.config.RetryConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RetryUtils {
    
    private final RetryConfig retryConfig;

    public RetryUtils(RetryConfig retryConfig) {
        this.retryConfig = retryConfig;
    }

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
     
        // Exponential back-off policy
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(retryConfig.getInitialIntervalMs());
        backOffPolicy.setMaxInterval(retryConfig.getMaxIntervalMs());
        backOffPolicy.setMultiplier(retryConfig.getMultiplier());
        retryTemplate.setBackOffPolicy(backOffPolicy);

        // Simple retry policy
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(retryConfig.getMaxAttempts());
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }

}
