package com.microservices.kafka.kafkaproducer.service.impl;

import javax.annotation.PreDestroy;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.microservices.kafka.kafkamodel.TwitterAvroModel;
import com.microservices.kafka.kafkaproducer.service.Producer;


@Service
public class TwitterProducer implements Producer<Long, TwitterAvroModel>{

    private static final Logger LOG = LoggerFactory.getLogger(TwitterProducer.class);

    private KafkaTemplate<Long, TwitterAvroModel> kafkaTemplate;
    
    public TwitterProducer(KafkaTemplate<Long, TwitterAvroModel> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void send(String topicName, Long key, TwitterAvroModel value) {
        LOG.info("Sending message to topic: {}, key: {}, value: {}", topicName, key, value);
        ListenableFuture<SendResult<Long, TwitterAvroModel>> kafkaResultFuture = kafkaTemplate.send(topicName, key, value);

        LOG.info("Added callback");

        // callback(topicName, value, kafkaResultFuture);

    }

    @PreDestroy
    public void close() {
        if (kafkaTemplate != null) {
            LOG.info("Closing kafka producer");
            kafkaTemplate.destroy();
        }
    }

    private void callback(String topicName, TwitterAvroModel message, ListenableFuture<SendResult<Long, TwitterAvroModel>> kafkaResultFuture) {
        kafkaResultFuture.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<Long, TwitterAvroModel> result) {
                RecordMetadata recordMetadata = result.getRecordMetadata();
                LOG.info("Sent Message");
                LOG.debug("Received new metadata. Topic: {}; Partition {}; Offset {}; Timestamp {}", 
                        recordMetadata.topic(),
                        recordMetadata.partition(), 
                        recordMetadata.offset(),
                        recordMetadata.timestamp());
            }

            @Override
            public void onFailure(Throwable t) {
                LOG.error("Error sending the message {} to the topic {}", message, topicName, t);
            }
        });
    }

}
