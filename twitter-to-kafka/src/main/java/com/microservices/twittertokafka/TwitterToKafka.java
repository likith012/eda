package com.microservices.twittertokafka;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.microservices.appconfigdata.config.TwitterToKafkaConfig;
import com.microservices.twittertokafka.runner.StreamRunner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;

@SpringBootApplication
@ComponentScan(basePackages = {"com.microservices.twittertokafka", "com.microservices.appconfigdata.config"})
public class TwitterToKafka implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterToKafka.class);
    
    private final TwitterToKafkaConfig configData;

    private final StreamRunner streamRunner;

    public TwitterToKafka(TwitterToKafkaConfig configData, StreamRunner streamRunner) {
        this.configData = configData;
        this.streamRunner = streamRunner;
    }

    public static void main(String[] args) {
        SpringApplication.run(TwitterToKafka.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        LOG.info(configData.getWelcomeMessage());
        LOG.info("Provided keywords are: {}", configData.getTwitterKeywords());

        streamRunner.start();
    }

}
