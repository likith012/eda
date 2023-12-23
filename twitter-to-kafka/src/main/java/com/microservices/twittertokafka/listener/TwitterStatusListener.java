package com.microservices.twittertokafka.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.microservices.appconfigdata.config.KafkaConfig;
import com.microservices.kafka.kafkamodel.TwitterAvroModel;
import com.microservices.kafka.kafkaproducer.service.Producer;
import com.microservices.twittertokafka.transformer.TwitterStatusToAvroTransformer;

import twitter4j.Status;
import twitter4j.StatusAdapter;

@Component
public class TwitterStatusListener extends StatusAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterStatusListener.class);

    private final KafkaConfig kafkaConfig;

    private final Producer<Long, TwitterAvroModel> producer;

    private final TwitterStatusToAvroTransformer transformer;

    public TwitterStatusListener(KafkaConfig kafkaConfig, Producer<Long, TwitterAvroModel> producer, TwitterStatusToAvroTransformer transformer) {
        this.kafkaConfig = kafkaConfig;
        this.producer = producer;
        this.transformer = transformer;
    }

    @Override
    public void onStatus(Status status) {
        LOG.info("@{} - {}", status.getUser().getScreenName(), status.getText());

        TwitterAvroModel twitterAvroModel = transformer.getAvroModelFromTwitter(status);
        producer.send(kafkaConfig.getTopicName(), twitterAvroModel.getUserId(), twitterAvroModel);
    }

    @Override
    public void onException(Exception ex) {
        LOG.error("Error receiving a tweet: {}", ex.getMessage());
    }

}
