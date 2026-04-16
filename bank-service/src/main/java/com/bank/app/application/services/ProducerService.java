package com.bank.app.application.services;

public interface ProducerService {
    void sendUserEvent(String clientId, Object data);
    void sendAccountEvent(String accountId, Object data);
}
