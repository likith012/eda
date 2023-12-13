package com.microservices.twittertokafka.runner.impl;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.microservices.appconfigdata.config.TwitterToKafkaConfig;
import com.microservices.twittertokafka.listener.TwitterStatusListener;
import com.microservices.twittertokafka.runner.StreamRunner;

import twitter4j.FilterQuery;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

@Component
@ConditionalOnProperty(name = "twitter-to-kafka-service.enable-mock-tweets", havingValue = "false", matchIfMissing = true)
public class TwitterStreamRunner implements StreamRunner{

    private static final Logger LOG = LoggerFactory.getLogger(TwitterStreamRunner.class);

    private TwitterStream twitterStream;

    private final TwitterToKafkaConfig configData;

    private final TwitterStatusListener twitterStatusListener;

    public TwitterStreamRunner(TwitterToKafkaConfig configData, TwitterStatusListener twitterStatusListener) {
        this.configData = configData;
        this.twitterStatusListener = twitterStatusListener;
    }

    @Override
    public void start() throws TwitterException {
        twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.addListener(twitterStatusListener);

        // Filtering using keywords
        addFilter();
    }

    private void addFilter() {
        String[] filterKeywords = configData.getTwitterKeywords().toArray(new String[0]);
        FilterQuery filterQuery = new FilterQuery(filterKeywords);
        twitterStream.filter(filterQuery);

        LOG.info("Started filtering twitter stream for keywords {}", configData.getTwitterKeywords());
    }

    @PreDestroy
    public void shutdown() {
        if (twitterStream != null) {
            LOG.info("Closing twitter stream!");
            twitterStream.shutdown();
        } else {
            LOG.warn("Twitter stream was null!");
        }
    }
    
}
