package com.microservices.twittertokafka.runner.impl;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.PreDestroy;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.microservices.appconfigdata.config.TwitterToKafkaConfig;
import com.microservices.twittertokafka.exception.TwitterServiceException;
import com.microservices.twittertokafka.listener.TwitterStatusListener;
import com.microservices.twittertokafka.runner.StreamRunner;
import com.microservices.twittertokafka.utils.RandomTweetGenerator;

import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

@Component
@ConditionalOnProperty(name = "twitter-to-kafka-service.enable-mock-tweets", havingValue = "true")
public class MockTwitterStreamRunner implements StreamRunner {

    private static final Logger LOG = LoggerFactory.getLogger(MockTwitterStreamRunner.class);

    private static final Random RANDOM = new Random();

    private static final String[] WORDS = new String[] {
        "Lorem",
        "ipsum",
        "dolor",
        "sit",
        "amet",
        "consectetur",
        "adipiscing",
        "elit",
        "sed",
        "do",
        "eiusmod",
        "tempor",
        "incididunt",
        "ut",
        "labore",
    };

    private static final String TWEET_JSON_TEMPLATE = "{" + 
        "\"created_at\": \"{0}\"," +
        "\"id\": \"{1}\"," +
        "\"text\": \"{2}\"," +
        "\"user\": {" +
            "\"id\": \"{3}\"," +
            "\"screen_name\": \"{4}\"" + 
        "}" +
        "}";

    private static final String TWITTER_STATUS_DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";

    private ExecutorService executorService;

    private Future<?> mockTwitterStreamFuture;

    private final TwitterToKafkaConfig configData;

    private final TwitterStatusListener twitterStatusListener;

    private final RandomTweetGenerator randomTweetGenerator;

    public MockTwitterStreamRunner(TwitterToKafkaConfig configData, TwitterStatusListener twitterStatusListener, RandomTweetGenerator randomTweetGenerator) {
        this.configData = configData;
        this.twitterStatusListener = twitterStatusListener;
        this.randomTweetGenerator = randomTweetGenerator;
    }
    
    @Override
    public void start() throws TwitterException {
        String[] filterKeywords = configData.getTwitterKeywords().toArray(new String[0]);
        int mockTweetMinLength = configData.getMockTweetMinLength();
        int mockTweetMaxLength = configData.getMockTweetMaxLength();
        long mockTweetSleepMs = configData.getMockTweetSleepMs();
        
        LOG.debug("Starting mock twitter stream on a separate thread with keywords {}", configData.getTwitterKeywords());

        simulateTwitterStream(filterKeywords, mockTweetMinLength, mockTweetMaxLength, mockTweetSleepMs);
    }

    private void simulateTwitterStream(String[] filterKeywords, int mockTweetMinLength, int mockTweetMaxLength, long mockTweetSleepMs) {
        executorService = Executors.newSingleThreadExecutor();
        mockTwitterStreamFuture = executorService.submit(() -> {
            LOG.info("Created a mocker server on a separate thread");

            try {
                while (true) {
                    String tweet = getRandomTweet(filterKeywords, mockTweetMinLength, mockTweetMaxLength);
                    LOG.debug("Mock tweet: {}", tweet);

                    Status status = TwitterObjectFactory.createStatus(tweet);
                    twitterStatusListener.onStatus(status);
                    sleep(mockTweetSleepMs);
                }
            } catch (TwitterException e) {
                LOG.error("Error while creating mock tweet", e);
            }
        });
    }

    private String getRandomTweet(String[] filterKeywords, int mockTweetMinLength, int mockTweetMaxLength) {
        String[] params = new String[] {
            ZonedDateTime.now().format(DateTimeFormatter.ofPattern(TWITTER_STATUS_DATE_FORMAT, Locale.ENGLISH)), // Date of the tweet
            String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)), // ID of the tweet
            getRandomText(filterKeywords, mockTweetMinLength, mockTweetMaxLength), // Text of the tweet 
            String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)), // ID of the user
            randomTweetGenerator.getRandomName() // Screen name of the user
        };

        String tweet = TWEET_JSON_TEMPLATE;
        for (int i = 0; i < params.length; i++) {
            tweet = tweet.replace("{" + i + "}", params[i]);
        }
        return tweet;
    }

    private String getRandomText(String[] filterKeywords, int mockTweetMinLength, int mockTweetMaxLength) {
        StringBuilder sb = new StringBuilder();
        int tweetLength = RANDOM.nextInt(mockTweetMaxLength - mockTweetMinLength + 1) + mockTweetMinLength;

        for (int i = 0; i < tweetLength; i++) {
            sb.append(WORDS[RANDOM.nextInt(WORDS.length)]); // Add random word
            sb.append(" ");

            if (i == tweetLength / 2) { // Add filter keyword in the middle of the tweet
                sb.append(filterKeywords[RANDOM.nextInt(filterKeywords.length)]);
                sb.append(" ");
            }
        }   
        return sb.toString().trim();
    }

    private void sleep(long mockTweetSleepMs) {
        try {
            Thread.sleep(mockTweetSleepMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Preserve interrupt status
            throw new TwitterServiceException("Error while waiting for new status to create", e);
        }
    }

    @PreDestroy
    public void shutdown() {
        if (mockTwitterStreamFuture != null) {
            mockTwitterStreamFuture.cancel(true); // Cancel the current task
        }

        if (executorService != null) {
            executorService.shutdown(); // Disable new tasks from being submitted and wait for existing tasks to terminate

            try {
                
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) { 
                    executorService.shutdownNow(); // Cancel currently executing tasks after waiting for 5 seconds

                    if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                        LOG.error("Executor service did not terminate"); // Log error if the executor service still doesn't terminate
                    }
                }

            } catch (InterruptedException e) {
                executorService.shutdownNow(); // When the main thread is interrupted, shutdown the executor service
                Thread.currentThread().interrupt(); // Preserve interrupt status
            }
        }
    }
}
