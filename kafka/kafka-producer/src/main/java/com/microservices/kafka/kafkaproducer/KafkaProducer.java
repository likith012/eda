package com.microservices.kafka.kafkaproducer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import com.microservices.appconfigdata.config.KafkaConfig;
import com.microservices.appconfigdata.config.KafkaProducerConfig;

@Configuration
public class KafkaProducer<K extends Serializable, V extends SpecificRecordBase> {
    
    private final KafkaProducerConfig kafkaProducerConfig;

    private final KafkaConfig kafkaConfig;

    public KafkaProducer(KafkaProducerConfig kafkaProducerConfig, KafkaConfig kafkaConfig) {
        this.kafkaProducerConfig = kafkaProducerConfig;
        this.kafkaConfig = kafkaConfig;
    }

    @Bean
    public Map<String, Object> producerConfig() {
        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers());
        props.put(kafkaConfig.getSchemaRegistryUrlKey(), kafkaConfig.getSchemaRegistryUrl());
        props.put(ProducerConfig.ACKS_CONFIG, kafkaProducerConfig.getAcks());
        props.put(ProducerConfig.RETRIES_CONFIG, kafkaProducerConfig.getRetryCount());
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, kafkaProducerConfig.getRequestTimeoutMs());
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, kafkaProducerConfig.getBatchSize() * kafkaProducerConfig.getBatchSizeBoostFactor());
        props.put(ProducerConfig.LINGER_MS_CONFIG, kafkaProducerConfig.getLingerMs());
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, kafkaProducerConfig.getCompressionType());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaProducerConfig.getKeySerializer());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaProducerConfig.getValueSerializer());

        return props;
    }

    @Bean
    public ProducerFactory<K, V> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<K, V> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
