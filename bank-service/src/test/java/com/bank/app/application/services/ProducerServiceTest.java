package com.bank.app.application.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ProducerServiceTest {

    @Mock
    private org.springframework.kafka.core.KafkaOperations<String, String> kafkaTemplate;

    @Mock
    private ProducerService producer;

    @Test
    void sendUserEvent_and_sendAccountEvent() {
        producer = new ProducerServiceImpl(kafkaTemplate);

        Object data = new Object();
        producer.sendUserEvent("1", data);
        producer.sendAccountEvent("2", data);

        verify(kafkaTemplate, times(1)).send(eq("user-topic"), eq("1"), anyString());
        verify(kafkaTemplate, times(1)).send(eq("account-topic"), eq("2"), anyString());
    }
}
