package com.bank.gateway.application.services;

import com.bank.gateway.data.entities.AccountDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class SecAccountService implements AccountClient{

    private final RestTemplate restTemplate;

    private final UserClient userClient;

    public SecAccountService(RestTemplate restTemplate, UserClient s) {
        this.restTemplate = restTemplate;
        userClient = s;
    }

    public AccountDTO getAccountById(int id) {
        String url = "http://localhost:8080/accounts/allAccounts";
        ResponseEntity<AccountDTO[]> response = restTemplate.getForEntity(url, AccountDTO[].class);
        Optional<AccountDTO> account = Arrays.stream(response.getBody())
                .filter(a -> a.getId() == id)
                .findFirst();
        return account.orElse(null);
    }

    public ResponseEntity<String> getUserInfo() {
        int userId = userClient.getCurrentUserId();
        String url = "http://localhost:8080/users/{id}";
        return restTemplate.getForEntity(url, String.class, userId);
    }

    public ResponseEntity<List<AccountDTO>> getUserAccounts() {
        int userId = userClient.getCurrentUserId();
        String url = "http://localhost:8080/accounts/allAccounts";
        ResponseEntity<AccountDTO[]> response = restTemplate.getForEntity(url, AccountDTO[].class);
        List<AccountDTO> accounts = Arrays.stream(response.getBody())
                .filter(account -> account.getOwnerId() == userId)
                .toList();
        return ResponseEntity.status(response.getStatusCode()).body(accounts);
    }

    public void addFriend(int id) {
        int userId = userClient.getCurrentUserId();
        String url = "http://localhost:8080/users/{id1}/addFriend";
        restTemplate.put(url, id, userId);
    }

    public void removeFriend(int id) {
        int userId = userClient.getCurrentUserId();
        String url = "http://localhost:8080/users/{id1}/deleteFriend";
        restTemplate.delete(url, id, userId);
    }

    public ResponseEntity<String> transfer(int fromId, int toId, int ownerId, double amount) {
        int userId = userClient.getCurrentUserId();

        if (ownerId != userId){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String url = "http://localhost:8080/accounts/transfer?fromId={fromId}&toId={toId}&amount={amount}";
        Map<String, Object> params = new HashMap<>();
        params.put("fromId", fromId);
        params.put("toId", toId);
        params.put("amount", amount);
        restTemplate.put(url, null, params);
        return ResponseEntity.ok("Success");
    }

    public ResponseEntity<String> withdraw(int ownerId, int id, double amount) {
        int userId = userClient.getCurrentUserId();
        if (ownerId != userId){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String url = "http://localhost:8080/accounts/{id}/withdraw";
        restTemplate.put(url, amount, id);
        return ResponseEntity.ok("Success");
    }

    public ResponseEntity<String> put(int ownerId, int id, double amount){
        int userId = userClient.getCurrentUserId();
        if (ownerId != userId){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String url = "http://localhost:8080/accounts/{id}/put";
        restTemplate.put(url, amount, id);
        return ResponseEntity.ok("Success");
    }
}
