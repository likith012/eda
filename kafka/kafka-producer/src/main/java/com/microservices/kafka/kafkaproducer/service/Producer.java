package com.microservices.kafka.kafkaproducer.service;

import java.io.Serializable;

import org.apache.avro.specific.SpecificRecordBase;

public interface Producer<K extends Serializable, V extends SpecificRecordBase> {
    
    void send(String topicName, K key, V value);

}
