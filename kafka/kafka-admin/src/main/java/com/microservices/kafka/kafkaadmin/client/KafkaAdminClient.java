package com.microservices.kafka.kafkaadmin.client;

import org.springframework.http.HttpStatus;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.microservices.appconfigdata.config.KafkaConfig;
import com.microservices.appconfigdata.config.RetryConfig;
import com.microservices.kafka.kafkaadmin.exception.KafkaClientException;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class KafkaAdminClient {
    
    private static final Logger LOG = LoggerFactory.getLogger(KafkaAdminClient.class);

    private final KafkaConfig kafkaConfig;

    private final RetryConfig retryConfig;

    private final AdminClient adminClient;

    private final RetryTemplate retryTemplate;

    private final WebClient webClient;

    public KafkaAdminClient(KafkaConfig kafkaConfig, RetryConfig retryConfig, AdminClient adminClient, RetryTemplate retryTemplate, WebClient webClient) {
        this.kafkaConfig = kafkaConfig;
        this.retryConfig = retryConfig;
        this.adminClient = adminClient;
        this.retryTemplate = retryTemplate;
        this.webClient = webClient;
    }
    
    public void createTopics() {
        CreateTopicsResult createTopicsResult;

        try{
            createTopicsResult = retryTemplate.execute(this::doCreateTopics);
            LOG.info("Topic(s) created: {}", createTopicsResult.values().keySet());
        } catch (Exception e) {
            throw new KafkaClientException("Reached max attempts for creating topics", e);
        }

        checkTopicsCreated();
    }

    private CreateTopicsResult doCreateTopics(RetryContext retryContext) {
        List<String> topicNames = kafkaConfig.getTopicNamesToCreate();

        LOG.info("Creating {} topics, retrying {} time", topicNames.size(), retryContext.getRetryCount());

        List<NewTopic> kafkaTopics = topicNames.stream().map(topic -> new NewTopic(
                topic.trim(), 
                kafkaConfig.getNumPartitions(), 
                kafkaConfig.getReplicationFactor()
        )).collect(Collectors.toList());

        return adminClient.createTopics(kafkaTopics);
    }

    public void checkTopicsCreated() {
        Collection<TopicListing> topics = getTopics();

        int retryCount = 1;
        Integer maxRetry = retryConfig.getMaxAttempts();
        Integer multiplier = retryConfig.getMultiplier().intValue();
        Long sleepTime = retryConfig.getSleepTimeMs();

        // Custom retry logic on top of RetryTemplate in `getTopics()`
        // Checks if each topic specified in `KafkaConfig` have been created, if not, retries
        for (String topicName : kafkaConfig.getTopicNamesToCreate()) {
            while (!isTopicCreated(topics, topicName)) {
                checkMaxRetry(retryCount++, maxRetry);
                sleep(sleepTime);
                sleepTime *= multiplier;
                topics = getTopics();
            }
        }
    }

    private boolean isTopicCreated(Collection<TopicListing> topics, String topicName) {
        if (topics.isEmpty()) {
            return false;
        }
        return topics.stream().anyMatch(topic -> topic.name().equals(topicName));
    }

    private Collection<TopicListing> getTopics() {
       Collection<TopicListing> topics;

       try {
           topics = retryTemplate.execute(this::doGetTopics);
       } catch (Exception e) {
           throw new KafkaClientException("Reached max attempts for getting topics", e);
       }

       return topics;
    }

    private Collection<TopicListing> doGetTopics(RetryContext retryContext) throws ExecutionException, InterruptedException {
        LOG.info("Getting topics {}, retrying {} time", kafkaConfig.getTopicNamesToCreate().toArray(), retryContext.getRetryCount());
        Collection<TopicListing> topics = adminClient.listTopics().listings().get();

        if (!topics.isEmpty()) {
            topics.forEach(topic -> LOG.debug("Topic name: {}, isInternal: {}", topic.name(), topic.isInternal()));
        }

        return topics;
    }

    public void checkSchemaRegistry() {
        int retryCount = 1;
        Integer maxRetry = retryConfig.getMaxAttempts();
        Integer multiplier = retryConfig.getMultiplier().intValue();
        Long sleepTime = retryConfig.getSleepTimeMs();

        while (!getSchemaRegistryStatus().is2xxSuccessful()) {
            checkMaxRetry(retryCount++, maxRetry);
            sleep(sleepTime);
            sleepTime *= multiplier;
        }
    }

    private HttpStatus getSchemaRegistryStatus() {
        HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;
        try {
            status =  webClient.get()
                .uri(kafkaConfig.getSchemaRegistryUrl())
                .exchange()
                .map(ClientResponse::statusCode)
                .block();
        } catch (Exception e) {
            return status;
        }

        return status != null ? status : HttpStatus.SERVICE_UNAVAILABLE;
    }

    private void checkMaxRetry(int retryCount, Integer maxRetry) {
        if (retryCount > maxRetry) {
            throw new KafkaClientException("Reached max attempts for checking topics");
        }
    }

    private void sleep(Long sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KafkaClientException("Error while retrying to check topics", e);
        }
    }

}
