package com.microservices.appconfigdata.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka-producer-config")
public class KafkaProducerConfig {
  
    private String acks;
    private Integer retryCount;
    private Integer requestTimeoutMs;
    private Integer batchSize;
    private Integer batchSizeBoostFactor;
    private Integer lingerMs;
    private String compressionType;
    private String keySerializer;
    private String valueSerializer;

}
