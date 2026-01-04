package com.bank.gateway.presentation.controllers;

import com.bank.gateway.data.entities.AccountDTO;
import com.bank.gateway.data.entities.HairColor;
import com.bank.gateway.data.entities.SecUserRequest;
import com.bank.gateway.application.services.AccountClient;
import com.bank.gateway.application.services.UserClient;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserClient userClient;

    private final AccountClient accountClient;

    public AdminController(UserClient u, AccountClient a) {
        userClient = u;
        accountClient = a;
    }

    @Operation(summary = "Создание пользователя")
    @PostMapping("/createUser")
    public ResponseEntity<String> createClient(SecUserRequest securityUser) {
        return userClient.createUser(securityUser);
    }

    @Operation(summary = "Вывод пользователей по фильтру")
    @GetMapping("/filter")
    public ResponseEntity<String> filterUsers(@RequestParam(required = false) HairColor hairColor, @RequestParam(required = false) String gender) {
        return userClient.getFilterUsers(hairColor, gender);
    }

    @Operation(summary = "Информация о пользователе")
    @GetMapping("/userInfo/{id}")
    public ResponseEntity<String> userInfo(@PathVariable int id) {
        return userClient.getUserInfo(id);
    }

    @Operation(summary = "Получение всех счетов")
    @GetMapping("/allAccounts")
    public ResponseEntity<String> getAccounts() {
        return userClient.getAccounts();
    }

    @Operation(summary = "Получение счетов пользователя")
    @GetMapping("/accountsByOwnerId/{id}")
    public ResponseEntity<List<AccountDTO>> getUserAccounts(@PathVariable int id) {
        return userClient.getAccounts(id);
    }

    @Operation(summary = "Получение счёта и истории его операций")
    @GetMapping("/accountById/{id}")
    public ResponseEntity<String> getAccountById(@PathVariable int id, @RequestParam String type) {
        String acc = accountClient.getAccountById(id).toString();
        return userClient.accountById(id, type, acc);
    }
}
