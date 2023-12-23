package com.microservices.twittertokafka.transformer;

import org.springframework.stereotype.Component;

import com.microservices.kafka.kafkamodel.TwitterAvroModel;

import twitter4j.Status;

@Component
public class TwitterStatusToAvroTransformer {
    
    public TwitterAvroModel getAvroModelFromTwitter(Status status) {
        return TwitterAvroModel
                .newBuilder()
                .setId(status.getId())
                .setUserId(status.getUser().getId())
                .setText(status.getText())
                .setCreatedAt(status.getCreatedAt().getTime())
                .build();
    }

}
