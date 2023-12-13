package com.microservices.twittertokafka.utils;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RandomTweetGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(RandomTweetGenerator.class);
    
    private final OkHttpClient httpClient = new OkHttpClient();

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getRandomName() {
        Request request = new Request.Builder()
                            .url("https://randomuser.me/api/")
                            .build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                LOG.error("Unexpected code {}", response);
                throw new IOException("Unexpected code " + response);
            }

            String responseBody = response.body().string();
            LOG.debug(responseBody);

            JsonNode rootNode = objectMapper.readTree(responseBody);
            String firstName = rootNode.path("results").path(0).path("name").path("first").asText();
            String lastName = rootNode.path("results").path(0).path("name").path("last").asText();

            return firstName + " " + lastName;
        } catch (IOException e) {
            LOG.error("Error while getting random name", e);
            return "John Doe";
        }
    }
}
