package com.bank.app.application.services;

import com.bank.app.application.dto.AccountDTO;
import com.bank.app.application.dto.OperationDTO;
import com.bank.app.application.exception.AccountNotFoundException;
import com.bank.app.application.exception.InsufficientFundsException;
import com.bank.app.application.exception.UserNotFoundException;
import com.bank.app.data.entities.Account;
import com.bank.app.data.entities.Operation;
import com.bank.app.data.entities.User;
import com.bank.app.data.repository.AccountRepository;
import com.bank.app.data.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для работы со счетами
 */
@Service
public class AccountServiceImpl implements AccountService
{
    private final AccountRepository accountRepo;

    private final UserRepository userRepo;

    private final ProducerService producer;

    /**
     * Конструктор
     * @param repository репозиторий счетов
     * @param userRepository репозиторий с пользователями
     */
    public AccountServiceImpl(AccountRepository repository, UserRepository userRepository, ProducerService producer)
    {
        accountRepo = repository;
        userRepo = userRepository;
        this.producer = producer;
    }

    /**
     * Метод для создания счёта
     * @param balance баланс
     * @param login логин владельца
     * @return результат
     */
    public AccountDTO createAccount(Double balance, String login, int ownerId)
    {
        Account account = new Account(balance, login, ownerId);
        Account save = accountRepo.save(account);
        producer.sendAccountEvent(String.valueOf(account.getId()), new AccountDTO(account));
        return new AccountDTO(save);
    }

    /**
     * Метод для проверки баланса счёта
     * @param id ид счёта
     * @return результат операции
     */
    public Double checkBalance(int id)
    {

        Account account = accountRepo.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account with id=" + id + " not found"));

        return account.getBalance();
    }

    /**
     * Метод для добавления денег на счёт
     * @param id ид счёта
     * @param amountOfMoney количество добавляемых денег
     */
    public void putMoney(int id, double amountOfMoney)
    {
        Account account = accountRepo.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account with id=" + id + " not found"));

        double resultAmountOfMoney = account.getBalance() + amountOfMoney;
        account.setBalance(resultAmountOfMoney);
        account.addOperation(new Operation("Put", amountOfMoney, account));
        accountRepo.save(account);
        producer.sendAccountEvent(String.valueOf(account.getId()), new AccountDTO(account));
    }

    /**
     * Метод для снятия денег со счёта
     * @param id ид счёта
     * @param amountOfMoney количество снимаемых денег
     */
    public void withdrawMoney(int id, double amountOfMoney)
    {
        Account account = accountRepo.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account with id=" + id + " not found"));

        double currentBalance = account.getBalance();
        if (currentBalance < amountOfMoney)
        {
            throw new InsufficientFundsException("Insufficient funds on the account with id=" + id);
        }

        double resultAmountOfMoney = currentBalance - amountOfMoney;
        account.setBalance(resultAmountOfMoney);
        account.addOperation(new Operation("Withdraw", amountOfMoney, account));
        accountRepo.save(account);
        producer.sendAccountEvent(String.valueOf(account.getId()), new AccountDTO(account));
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
     */
    public void moneyTransaction(int fromId, int toId, double amountOfMoney)
    {
        Account fromAccount = accountRepo.findById(fromId)
                .orElseThrow(() -> new AccountNotFoundException("Account with id=" + fromId + " not found"));

        Account toAccount = accountRepo.findById(toId)
                .orElseThrow(() -> new AccountNotFoundException("Account with id=" + toId + " not found"));

        User sender = userRepo.findById(fromAccount.getOwnerId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        double percentage3 = calculateCommission(amountOfMoney, 3);
        double percentage10 = calculateCommission(amountOfMoney, 10);
        double currentBalanceFrom = fromAccount.getBalance();
        double currentBalanceTo = toAccount.getBalance();

        if(fromAccount.getLogin().equals(toAccount.getLogin()))
        {
            if (currentBalanceFrom < amountOfMoney)
            {
                throw new InsufficientFundsException("Insufficient funds on the account with id=" + fromAccount);
            }

            toAccount.setBalance(currentBalanceTo + amountOfMoney);
            fromAccount.setBalance(currentBalanceFrom - amountOfMoney);
            toAccount.addOperation(new Operation("Put", amountOfMoney, toAccount));
            fromAccount.addOperation(new Operation("Withdraw", amountOfMoney, fromAccount));
            accountRepo.save(fromAccount);
            accountRepo.save(toAccount);
        }

        else if (sender.isFriend(toAccount.getLogin()))
        {
            if (fromAccount.getBalance() < amountOfMoney + percentage3)
            {
                throw new InsufficientFundsException("Insufficient funds on the account with id=" + fromAccount);
            }

            fromAccount.setBalance(currentBalanceFrom - amountOfMoney - percentage3);
            toAccount.setBalance(currentBalanceTo + amountOfMoney);
            toAccount.addOperation(new Operation("Put", amountOfMoney, toAccount));
            fromAccount.addOperation(new Operation("Withdraw", amountOfMoney + percentage3, fromAccount));
            accountRepo.save(fromAccount);
            accountRepo.save(toAccount);
        }

        else
        {
            if (fromAccount.getBalance() < amountOfMoney + percentage10)
            {
                throw new InsufficientFundsException("Insufficient funds on the account with id=" + fromAccount);
            }

            fromAccount.setBalance(currentBalanceFrom - amountOfMoney - percentage10);
            toAccount.setBalance(currentBalanceTo + amountOfMoney);
            toAccount.addOperation(new Operation("Put", amountOfMoney, toAccount));
            fromAccount.addOperation(new Operation("Withdraw", amountOfMoney + percentage10, fromAccount));
            accountRepo.save(fromAccount);
            accountRepo.save(toAccount);
        }

        producer.sendAccountEvent(String.valueOf(fromAccount.getId()), new AccountDTO(fromAccount));
        producer.sendAccountEvent(String.valueOf(toAccount.getId()), new AccountDTO(toAccount));
    }

    /**
     * Метод дял получения операций по счёту конкретного типа
     * @param id ид счёта
     * @param type тип операции
     * @return список операций
     */
    public List<OperationDTO> getOperationByTypeAndId(int id, String type) {
        Account account = accountRepo.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account with id=" + id + " not found"));

        return account.getOperationHistory()
                .stream()
                .filter(x -> x.getName().equals(type))
                .map(OperationDTO::new)
                .toList();
    }

    /**
     * Получение всех счетов
     * @return список счетов
     */
    public List<AccountDTO> getAllAccounts() {
        return new ArrayList<>(accountRepo.findAll().stream().map(AccountDTO::new).toList());
    }
}
