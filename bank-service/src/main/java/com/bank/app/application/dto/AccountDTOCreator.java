package com.bank.app.application.dto;

import com.bank.app.data.entities.Account;

/**
 * Класс для создания дто счёта
 */
public class AccountDTOCreator {
    public static AccountDTO toDTO(Account account) {
        return new AccountDTO(
                account.getId(),
                account.getBalance(),
                account.getLogin(),
                account.getOwnerId()
        );
    }
}
