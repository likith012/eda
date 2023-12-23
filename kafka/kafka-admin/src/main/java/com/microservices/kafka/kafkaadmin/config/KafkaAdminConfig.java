package com.microservices.kafka.kafkaadmin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.CommonClientConfigs;
import com.microservices.appconfigdata.config.KafkaConfig;
import java.util.Map;

@Configuration
public class KafkaAdminConfig {
    
    private final KafkaConfig kafkaConfig;

    public KafkaAdminConfig(KafkaConfig kafkaConfig) {
        this.kafkaConfig = kafkaConfig;
    }

    @Bean
    public AdminClient adminClient() {
        return AdminClient.create(Map.of(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers()));
    }

}
