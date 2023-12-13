package com.microservices.appconfigdata;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;

@SpringBootApplication
public class AppConfigData {

    private static final Logger LOG = LoggerFactory.getLogger(AppConfigData.class);

    public static void main(String[] args) {
        SpringApplication.run(AppConfigData.class, args);
        LOG.info("AppConfigData started...");
    }

}
