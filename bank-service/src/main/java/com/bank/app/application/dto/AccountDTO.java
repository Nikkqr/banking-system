package com.bank.app.application.dto;

import com.bank.app.data.entities.Account;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * дто для счёта
 */
@Data
public class AccountDTO {
    private int id;

    private Double balance;

    private String login;

    private int ownerId;

    private List<OperationDTO> operations = new ArrayList<>();

    public AccountDTO() {}

    public AccountDTO(Account account)
    {
        id = account.getId();
        balance = account.getBalance();
        login = account.getLogin();
        ownerId = account.getOwnerId();
        operations = account.getOperationHistory().stream().map(OperationDTO::new).toList();
    }
}
