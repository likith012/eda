package com.microservices.twittertokafka.runner;

import twitter4j.TwitterException;

public interface StreamRunner {

    void start() throws TwitterException;
    
}
