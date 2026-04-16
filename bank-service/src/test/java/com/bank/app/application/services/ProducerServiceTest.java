package com.bank.app.application.services;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProducerServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void sendUserEvent_and_sendAccountEvent() {
        ProducerService producer = new ProducerService(kafkaTemplate);

        Object data = new Object();
        producer.sendUserEvent("1", data);
        producer.sendAccountEvent("2", data);

        verify(kafkaTemplate, times(1)).send(eq("user-topic"), eq("1"), anyString());
        verify(kafkaTemplate, times(1)).send(eq("account-topic"), eq("2"), anyString());
    }
}

