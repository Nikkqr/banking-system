package com.bank.gateway.data.entities;

import lombok.Data;

@Data
public class AccountDTO {

    private int id;

    private Double balance;

    private String login;

    private int ownerId;

    public AccountDTO() {}

    public AccountDTO(Double balance, String login, int ownerId)
    {
        this.balance = balance;
        this.login = login;
        this.ownerId = ownerId;
    }

}
