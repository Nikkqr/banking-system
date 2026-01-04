package com.bank.gateway.application.services;

import com.bank.gateway.data.entities.AccountDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AccountClient {

    AccountDTO getAccountById(int id);

    ResponseEntity<String> getUserInfo();

    ResponseEntity<List<AccountDTO>> getUserAccounts();

    void addFriend(int id);

    void removeFriend(int id);

    ResponseEntity<String> transfer(int fromId, int toId, int ownerId, double amount);

    ResponseEntity<String> withdraw(int ownerId, int id, double amount);

    ResponseEntity<String> put(int ownerId, int id, double amount);
}
