package com.bank.app.application.dto;
import lombok.Data;

@Data
public class CreateAccountRequest {

    private Double balance;

    private String login;

    private int ownerId;
}
