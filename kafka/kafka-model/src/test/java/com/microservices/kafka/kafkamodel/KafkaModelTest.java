package com.microservices.kafka.kafkamodel;

import com.microservices.kafka.kafkamodel.KafkaModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class KafkaModelTest {
    private KafkaModel kafkaModel;

    @BeforeEach
    public void setUp() {
        kafkaModel = new KafkaModel();
    }

    @Test
    public void testUserId() {
        long userId = 123L;
        kafkaModel.setUserId(userId);
        assertEquals(userId, kafkaModel.getUserId());
    }

    @Test
    public void testId() {
        long id = 456L;
        kafkaModel.setId(id);
        assertEquals(id, kafkaModel.getId());
    }

    @Test
    public void testText() {
        String text = "Test text";
        kafkaModel.setText(text);
        assertEquals(text, kafkaModel.getText());
    }

    @Test
    public void testCreatedAt() {
        long createdAt = System.currentTimeMillis();
        kafkaModel.setCreatedAt(createdAt);
        assertEquals(createdAt, kafkaModel.getCreatedAt());
    }
}
