package com.bank.app.application.services;

import com.google.gson.Gson;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.stereotype.Service;

@Service
public class ProducerServiceImpl implements ProducerService {

    private final KafkaOperations<String, String> kafkaTemplate;

    public ProducerServiceImpl(KafkaOperations<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendUserEvent(String clientId, Object data) {
        String json = new Gson().toJson(data);
        kafkaTemplate.send("user-topic", clientId, json);
    }

    @Override
    public void sendAccountEvent(String accountId, Object data) {
        String json = new Gson().toJson(data);
        kafkaTemplate.send("account-topic", accountId, json);
    }
}
