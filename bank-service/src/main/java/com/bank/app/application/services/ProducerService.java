package com.bank.app.application.services;

import com.google.gson.Gson;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public ProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUserEvent(String clientId, Object data) {
        String json = new Gson().toJson(data);
        kafkaTemplate.send("user-topic", clientId, json);
    }

    public void sendAccountEvent(String accountId, Object data) {
        String json = new Gson().toJson(data);
        kafkaTemplate.send("account-topic", accountId, json);
    }
}
