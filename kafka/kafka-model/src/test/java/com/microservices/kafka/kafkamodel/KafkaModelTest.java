package com.microservices.kafka.kafkamodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class KafkaModelTest {
    private KafkaModel kafkaModel;

    @BeforeEach
    public void setUp() {
        kafkaModel = new KafkaModel();
    }

    @Test
    void testUserId() {
        long userId = 123L;
        kafkaModel.setUserId(userId);
        assertEquals(userId, kafkaModel.getUserId());
    }

    @Test
    void testId() {
        long id = 456L;
        kafkaModel.setId(id);
        assertEquals(id, kafkaModel.getId());
    }

    @Test
    void testText() {
        String text = "Test text";
        kafkaModel.setText(text);
        assertEquals(text, kafkaModel.getText());
    }

    @Test
    void testCreatedAt() {
        long createdAt = System.currentTimeMillis();
        kafkaModel.setCreatedAt(createdAt);
        assertEquals(createdAt, kafkaModel.getCreatedAt());
    }
}
