package com.bank.app.presentation.controllers;

import com.bank.app.application.dto.*;
import com.bank.app.application.services.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Контроллер отвечающий за операции со счетами
 */
@RestController
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(summary = "Создание нового счёта")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Счёт успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")
    })
    @PostMapping("")
    public ResponseEntity<AccountDTO> createAccount(@RequestBody CreateAccountRequest request) {
        AccountDTO res = accountService.createAccount(
                request.getBalance(),
                request.getLogin(),
                request.getOwnerId()
        );

        return ResponseEntity.ok(res);
    }

    @Operation(summary = "Получение баланса счёта по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Баланс успешно получен"),
            @ApiResponse(responseCode = "404", description = "Счёт не найден")
    })
    @GetMapping("/{id}/balance")
    public ResponseEntity<Double> checkBalance(@PathVariable int id) {
        Double balance = accountService.checkBalance(id);
        return ResponseEntity.ok(balance);
    }

    @Operation(summary = "Пополнение баланса счёта")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Баланс успешно пополнен"),
            @ApiResponse(responseCode = "404", description = "Счёт не найден")
    })
    @PutMapping("/{id}/put")
    public void putMoney(@PathVariable int id, @RequestBody double amount) {
        accountService.putMoney(id, amount);
    }

    @Operation(summary = "Снятие денег со счёта")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Снятие прошло успешно"),
            @ApiResponse(responseCode = "400", description = "Недостаточно средств"),
            @ApiResponse(responseCode = "404", description = "Счёт не найден")
    })
    @PutMapping("/{id}/withdraw")
    public ResponseEntity<Void> withdrawMoney(@PathVariable int id, @RequestBody double amount) {
        accountService.withdrawMoney(id, amount);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Перевод денег между счетами")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Перевод успешно выполнен"),
            @ApiResponse(responseCode = "400", description = "Ошибка перевода"),
            @ApiResponse(responseCode = "404", description = "Один из счетов не найден")
    })
    @PutMapping("/transfer")
    public ResponseEntity<Void> transferMoney(@RequestParam int fromId, @RequestParam int toId, @RequestParam double amount) {
        accountService.moneyTransaction(fromId, toId, amount);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получение операций конкретного типа по счёту ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Операции найдены"),
            @ApiResponse(responseCode = "404", description = "Операции не найдены")
    })
    @GetMapping("/{id}/operation")
    public ResponseEntity<List<OperationDTO>> operationHistory(@PathVariable int id, @RequestParam String type) {
        List<OperationDTO> operations = accountService.getOperationByTypeAndId(id, type);
        if (operations.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(operations);
    }

    @Operation(summary = "Вывод всех счетов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Счета успешно найдены"),
            @ApiResponse(responseCode = "404", description = "Счета не найдены")
    })
    @GetMapping("/allAccounts")
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {
        List<AccountDTO> accounts = accountService.getAllAccounts();
        if (accounts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(accounts);
    }

}
