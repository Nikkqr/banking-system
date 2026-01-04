package com.bank.app.application.services;

import com.bank.app.application.dto.AccountDTOCreator;
import com.bank.app.data.entities.Account;
import com.bank.app.data.entities.Operation;
import com.bank.app.data.entities.User;
import com.bank.app.data.repository.AccountRepository;
import com.bank.app.data.repository.UserRepository;
import com.bank.app.data.resultType.Result;
import com.bank.app.data.resultType.ResultDescription;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для работы со счетами
 */
@Service
public class AccountService
{
    private final AccountRepository repo;

    private final UserRepository userRepo;

    private final ProducerService producer;

    /**
     * Конструктор
     * @param repository репозиторий счетов
     * @param userRepository репозиторий с пользователями
     */
    public AccountService(AccountRepository repository, UserRepository userRepository, ProducerService producer)
    {
        repo = repository;
        userRepo = userRepository;
        this.producer = producer;
    }

    /**
     * Метод для создания счёта
     * @param balance баланс
     * @param login логин владельца
     * @return результат
     */
    public Result createAccount(Double balance, String login, int ownerId)
    {
        Account account = new Account(balance, login, ownerId);
        repo.createAccount(account);
        producer.sendAccountEvent(String.valueOf(account.getId()), AccountDTOCreator.toDTO(account));
        return new Result(ResultDescription.TheAccountHasBeenSuccessfullyCreated, account);
    }

    /**
     * Метод для проверки баланса счёта
     * @param id ид счёта
     * @return результат операции
     */
    public Result checkBalance(int id)
    {
        Account account = repo.getAccountById(id).getAccount();
        return new Result(ResultDescription.SuccessfullyPrinted, account.getBalance());
    }

    /**
     * Метод для добавления денег на счёт
     * @param id ид счёта
     * @param amountOfMoney количество добавляемых денег
     * @return результат операции
     */
    public Result putMoney(int id, double amountOfMoney)
    {
        Account account = repo.getAccountById(id).getAccount();
        double resultAmountOfMoney = account.getBalance() + amountOfMoney;
        account.setBalance(resultAmountOfMoney);
        account.addOperation(new Operation("Put", amountOfMoney, account));
        repo.updateAccount(account);
        producer.sendAccountEvent(String.valueOf(account.getId()), AccountDTOCreator.toDTO(account));
        return new Result(ResultDescription.TheMoneyIsPutToAccount);
    }

    /**
     * Метод для снятия денег со счёта
     * @param id ид счёта
     * @param amountOfMoney количество снимаемых денег
     * @return результат операции
     */
    public Result withdrawMoney(int id, double amountOfMoney)
    {
        Account account = repo.getAccountById(id).getAccount();
        double currentBalance = account.getBalance();
        if (currentBalance < amountOfMoney)
        {
            return new Result(ResultDescription.ThereIsNotEnoughMoneyInTheAccount);
        }

        double resultAmountOfMoney = currentBalance - amountOfMoney;
        account.setBalance(resultAmountOfMoney);
        account.addOperation(new Operation("Withdraw", amountOfMoney, account));
        repo.updateAccount(account);
        producer.sendAccountEvent(String.valueOf(account.getId()), AccountDTOCreator.toDTO(account));
        return new Result(ResultDescription.TheMoneyIsWithdrawToAccount);
    }

    /**
     * Метод для подсчёта комиссии
     * @param amountOfMoney количество денег
     * @param percent процент комиссии
     * @return комиссия для суммы
     */
    public double calculateCommission(double amountOfMoney, double percent){
        return amountOfMoney * (percent / 100);
    }

    /**
     * Метод для перевода денег между счетами
     * @param fromId ид счёта отправителя
     * @param toId ид счёта получателя
     * @param amountOfMoney количество переводимых денег
     * @return результат
     */
    public Result moneyTransaction(int fromId, int toId, double amountOfMoney)
    {
        Account fromAccount = repo.getAccountById(fromId).getAccount();
        Account toAccount = repo.getAccountById(toId).getAccount();
        User sender = userRepo.getUserById(fromAccount.getOwnerId()).getUser();
        double percentage3 = calculateCommission(amountOfMoney, 3);
        double percentage10 = calculateCommission(amountOfMoney, 10);
        double currentBalanceFrom = fromAccount.getBalance();
        double currentBalanceTo = toAccount.getBalance();

        if(fromAccount.getLogin().equals(toAccount.getLogin()))
        {
            if (currentBalanceFrom < amountOfMoney)
            {
                return new Result(ResultDescription.ThereIsNotEnoughMoneyInTheAccount);
            }

            toAccount.setBalance(currentBalanceTo + amountOfMoney);
            fromAccount.setBalance(currentBalanceFrom - amountOfMoney);
            toAccount.addOperation(new Operation("Put", amountOfMoney, toAccount));
            fromAccount.addOperation(new Operation("Withdraw", amountOfMoney, fromAccount));
            repo.updateAccount(fromAccount);
            repo.updateAccount(toAccount);
        }

        else if (sender.isFriend(toAccount.getLogin()))
        {
            if (fromAccount.getBalance() < amountOfMoney + percentage3)
            {
                return new Result(ResultDescription.ThereIsNotEnoughMoneyInTheAccount);
            }

            fromAccount.setBalance(currentBalanceTo - amountOfMoney - percentage3);
            toAccount.setBalance(currentBalanceFrom + amountOfMoney);
            toAccount.addOperation(new Operation("Put", amountOfMoney, toAccount));
            fromAccount.addOperation(new Operation("Withdraw", amountOfMoney + percentage3, fromAccount));
            repo.updateAccount(fromAccount);
            repo.updateAccount(toAccount);
        }

        else
        {
            if (fromAccount.getBalance() < amountOfMoney + percentage10)
            {
                return new Result(ResultDescription.ThereIsNotEnoughMoneyInTheAccount);
            }

            fromAccount.setBalance(currentBalanceTo - amountOfMoney - percentage10);
            toAccount.setBalance(currentBalanceFrom + amountOfMoney);
            toAccount.addOperation(new Operation("Put", amountOfMoney, toAccount));
            fromAccount.addOperation(new Operation("Withdraw", amountOfMoney + percentage10, fromAccount));
            repo.updateAccount(fromAccount);
            repo.updateAccount(toAccount);
        }

        producer.sendAccountEvent(String.valueOf(fromAccount.getId()), AccountDTOCreator.toDTO(fromAccount));
        producer.sendAccountEvent(String.valueOf(toAccount.getId()), AccountDTOCreator.toDTO(toAccount));
        return new Result(ResultDescription.TheMoneyIsPutToAccount);
    }

    /**
     * Метод дял получения операций по счёту конкретного типа
     * @param id ид счёта
     * @param type тип операции
     * @return список операций
     */
    public List<Operation> getOperationByTypeAndId(int id, String type) {
        List<Operation> operations = new ArrayList<>();
        for(Operation o : repo.getAccountById(id).getAccount().getOperationHistory()) {
            if(o.getName().equals(type)) {
                operations.add(o);
            }
        }
        return operations;
    }

    /**
     * Получение всех пользователей
     * @return список пользователей
     */
    public List<Account> getAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        accounts.addAll(repo.findAll());
        return accounts;
    }
}
