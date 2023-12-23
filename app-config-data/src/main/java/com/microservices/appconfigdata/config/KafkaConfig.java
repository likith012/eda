package com.microservices.appconfigdata.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka-config")
public class KafkaConfig {

    private String bootstrapServers;
    private String schemaRegistryUrl;
    private String schemaRegistryUrlKey;
    private String topicName;
    private List<String> topicNamesToCreate;
    private Integer numPartitions;
    private Short replicationFactor;
    
}
