package com.bank.storage.presentation.controllers;

import com.bank.storage.application.services.ConsumerService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ConsumerController {

    ConsumerService consumerService;

    public ConsumerController(ConsumerService consumerService) {
        this.consumerService = consumerService;
    }

    @KafkaListener(topics = "user-topic", groupId = "group")
    public void consumeUser(ConsumerRecord<String, String> record) {
        consumerService.saveUserEvent(record.key(), record.value());

    }

    @KafkaListener(topics = "account-topic", groupId = "group")
    public void consumeAccount(ConsumerRecord<String, String> record) {
        consumerService.saveAccountEvent(record.key(), record.value());
    }
}
