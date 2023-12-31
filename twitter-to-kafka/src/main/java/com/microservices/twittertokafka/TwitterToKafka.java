package com.microservices.twittertokafka;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.microservices.twittertokafka.init.StreamInitializer;
import com.microservices.twittertokafka.runner.StreamRunner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;

@SpringBootApplication
@ComponentScan(basePackages = {
    "com.microservices.twittertokafka",
    "com.microservices.appconfigdata.config",
    "com.microservices.commonutils",
    "com.microservices.kafka.kafkaadmin",
    "com.microservices.kafka.kafkaproducer",
    "com.microservices.kafka.kafkamodel"
})
public class TwitterToKafka implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterToKafka.class);
    
    private final StreamInitializer streamInitializer;

    private final StreamRunner streamRunner;

    public TwitterToKafka(StreamInitializer streamInitializer, StreamRunner streamRunner) {
        this.streamInitializer = streamInitializer;
        this.streamRunner = streamRunner;
    }

    public static void main(String[] args) {
        SpringApplication.run(TwitterToKafka.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        LOG.info("Application started ...");

        streamInitializer.init();
        streamRunner.start();
    }

}
