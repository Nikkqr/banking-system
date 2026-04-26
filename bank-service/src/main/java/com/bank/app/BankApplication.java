package com.bank.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс для запуска программы
 */
@SpringBootApplication
public class BankApplication
{
    public static void main(String[] args) {
        SpringApplication.run(BankApplication.class, args);
    }
}