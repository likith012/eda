package com.microservices.twittertokafka.init.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.microservices.appconfigdata.config.KafkaConfig;
import com.microservices.kafka.kafkaadmin.client.KafkaAdminClient;
import com.microservices.twittertokafka.init.StreamInitializer;

@Component
public class KafkaStreamInitializer implements StreamInitializer{
    
    private static final Logger LOG = LoggerFactory.getLogger(KafkaStreamInitializer.class);

    private final KafkaAdminClient kafkaAdminClient;

    private final KafkaConfig kafkaConfig;

    public KafkaStreamInitializer(KafkaAdminClient kafkaAdminClient, KafkaConfig kafkaConfig) {
        this.kafkaAdminClient = kafkaAdminClient;
        this.kafkaConfig = kafkaConfig;
    }

    @Override
    public void init() {
        kafkaAdminClient.createTopics();
        kafkaAdminClient.checkSchemaRegistry();
        LOG.info("Topics with name {} created successfully", kafkaConfig.getTopicNamesToCreate().toArray());
    }

}
  