package com.bank.app.application.exception;

public class AccountNotFoundException extends RuntimeException{
    public AccountNotFoundException(String message) {
        super(message);
    }
}
