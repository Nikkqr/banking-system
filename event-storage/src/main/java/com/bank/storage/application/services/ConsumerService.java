package com.bank.storage.application.services;

import com.bank.storage.data.events.AccountEvent;
import com.bank.storage.data.events.UserEvent;
import com.bank.storage.data.repository.UserEventRepository;
import com.bank.storage.data.repository.AccountEventRepository;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {

    private final UserEventRepository userRepo;
    private final AccountEventRepository accountRepo;

    public ConsumerService(UserEventRepository clientRepo, AccountEventRepository accountRepo) {
        this.userRepo = clientRepo;
        this.accountRepo = accountRepo;
    }

    public void saveUserEvent(String key, String value) {
        UserEvent event = new UserEvent(key, value);
        userRepo.createUserEvent(event);
    }

    public void saveAccountEvent(String key, String value) {
        AccountEvent event = new AccountEvent(key, value);
        accountRepo.createAccountEvent(event);
    }
}
