package com.bank.gateway.presentation.controllers;

import com.bank.gateway.data.entities.AccountDTO;
import com.bank.gateway.application.services.AccountClient;
import com.bank.gateway.application.services.UserClient;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/client")
@PreAuthorize("hasRole('CLIENT')")
public class ClientController {

    private final UserClient userClient;

    private final AccountClient accountClient;

    public ClientController(AccountClient a, UserClient u) {
        accountClient = a;
        userClient = u;
    }

    @Operation(summary = "Информация о пользователе")
    @GetMapping("/userInfo")
    public ResponseEntity<String> userInfo() {
        ResponseEntity<String> response = accountClient.getUserInfo();
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    @Operation(summary = "Все счета пользователя")
    @GetMapping("/userAccounts")
    public ResponseEntity<List<AccountDTO>> userAccounts() {
        ResponseEntity<List<AccountDTO>> response = accountClient.getUserAccounts();
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    @Operation(summary = "Вывод информации о счёте")
    @GetMapping("/accountInfo/{id}")
    public ResponseEntity<String> userAccountInfo(@PathVariable int id) {
        int userId = userClient.getCurrentUserId();
        int ownerId = accountClient.getAccountById(id).getOwnerId();

        if (ownerId != userId){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String acc = accountClient.getAccountById(id).toString();
        return ResponseEntity.ok(acc);
    }

    @Operation(summary = "Добавление друга")
    @PutMapping("/addFriend")
    public void addFriendForUser(@RequestBody  int id) {
        accountClient.addFriend(id);
    }

    @Operation(summary = "Удаление друга")
    @DeleteMapping("/removeFriend")
    public void removeUserFriend(@RequestBody int id) {
        accountClient.removeFriend(id);
    }

    @Operation(summary = "Перевод денег со счёта на счёт")
    @PutMapping("/transfer")
    public ResponseEntity<String> transferMoney(@RequestParam int fromId, @RequestParam int toId, @RequestParam double amount) {
        int ownerId = accountClient.getAccountById(fromId).getOwnerId();
        return accountClient.transfer(fromId, toId, ownerId, amount);
    }

    @Operation(summary = "Снятие денег со счёта")
    @PutMapping("/withdraw")
    public ResponseEntity<String> withdrawMoney(@RequestParam int id, @RequestParam double amount) {
        int ownerId = accountClient.getAccountById(id).getOwnerId();
        return accountClient.withdraw(ownerId, id, amount);
    }

    @Operation(summary = "Пополнение счёта")
    @PutMapping("/put")
    public ResponseEntity<String> putMoney(@RequestParam int id, @RequestParam double amount) {
        int ownerId = accountClient.getAccountById(id).getOwnerId();
        return accountClient.put(ownerId, id, amount);
    }
}
