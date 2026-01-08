package com.bank.app.presentation.exception;

import com.bank.app.application.exception.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MyExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException e) {
        return ResponseEntity.status(404).body(new ErrorResponse("USER_NOT_FOUND", e.getMessage()));
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFound(AccountNotFoundException e) {
        return ResponseEntity.status(404).body(new ErrorResponse("ACCOUNT_NOT_FOUND", e.getMessage()));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException e) {
        return ResponseEntity.status(400).body(new ErrorResponse("USER_ALREADY_EXISTS", e.getMessage()));
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientFunds(InsufficientFundsException e) {
        return ResponseEntity.status(400).body(new ErrorResponse("INSUFFICIENT_FUNDS", e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception e) {
        return ResponseEntity.status(500).body(new ErrorResponse("INTERNAL_ERROR", "An internal error has occurred"));
    }

    public record ErrorResponse(String code, String message) {}
}