package com.bank.app.application.services;

import com.bank.app.application.dto.AccountDTO;
import com.bank.app.application.dto.OperationDTO;

import java.util.List;

public interface AccountService {
	AccountDTO createAccount(Double balance, String login, int ownerId);

	Double checkBalance(int id);

	void putMoney(int id, double amountOfMoney);

	void withdrawMoney(int id, double amountOfMoney);

	double calculateCommission(double amountOfMoney, double percent);

	void moneyTransaction(int fromId, int toId, double amountOfMoney);

	List<OperationDTO> getOperationByTypeAndId(int id, String type);

	List<AccountDTO> getAllAccounts();
}
