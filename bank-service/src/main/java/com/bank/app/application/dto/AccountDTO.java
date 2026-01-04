package com.bank.app.application.dto;

import com.bank.app.data.entities.Operation;
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

    private List<Operation> operations;

    public AccountDTO() {}

    public AccountDTO(int id, Double balance, String login, int ownerId)
    {
        this.id = id;
        this.balance = balance;
        this.login = login;
        this.ownerId = ownerId;
        operations = new ArrayList<>();
    }
}
