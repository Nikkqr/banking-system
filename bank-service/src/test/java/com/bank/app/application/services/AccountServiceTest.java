package com.bank.app.application.services;

import com.bank.app.application.dto.AccountDTO;
import com.bank.app.application.dto.OperationDTO;
import com.bank.app.application.exception.AccountNotFoundException;
import com.bank.app.application.exception.InsufficientFundsException;
import com.bank.app.data.entities.Account;
import com.bank.app.data.entities.Operation;
import com.bank.app.data.entities.User;
import com.bank.app.data.repository.AccountRepository;
import com.bank.app.data.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository repo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private ProducerService producer;

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountServiceImpl(repo, userRepo, producer);
    }

    @Test
    void createAccount_success() {
        when(repo.save(any(Account.class))).thenAnswer(invocation -> {
            Account a = invocation.getArgument(0);
            Account saved = new Account(a.getBalance(), a.getLogin(), a.getOwnerId());
            saved.setId(10);
            return saved;
        });

        AccountDTO dto = accountService.createAccount(100.0, "login", 5);

        assertNotNull(dto);
        assertEquals(10, dto.getId());
        verify(repo, times(1)).save(any(Account.class));
        verify(producer, times(1)).sendAccountEvent(anyString(), any(AccountDTO.class));
    }

    @Test
    void checkBalance_success_and_notFound() {
        Account acc = new Account(50.0, "l", 1);
        acc.setId(2);
        when(repo.findById(2)).thenReturn(Optional.of(acc));

        Double b = accountService.checkBalance(2);
        assertEquals(50.0, b);

        when(repo.findById(99)).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class, () -> accountService.checkBalance(99));
    }

    @Test
    void putMoney_and_withdrawMoney_success_and_insufficient() {
        Account acc = new Account(100.0, "l", 1);
        acc.setId(3);
        when(repo.findById(3)).thenReturn(Optional.of(acc));

        accountService.putMoney(3, 50.0);
        assertEquals(150.0, acc.getBalance());
        verify(repo, times(1)).save(acc);
        verify(producer, times(1)).sendAccountEvent(anyString(), any(AccountDTO.class));

        // withdraw success
        accountService.withdrawMoney(3, 30.0);
        assertEquals(120.0, acc.getBalance());
        verify(repo, times(2)).save(acc);

        // withdraw insufficient
        assertThrows(InsufficientFundsException.class, () -> accountService.withdrawMoney(3, 1000.0));
    }

    @Test
    void calculateCommission() {
        double commission = accountService.calculateCommission(200.0, 5);
        assertEquals(10.0, commission);
    }

    @Test
    void moneyTransaction_sameLogin_success() {
        Account from = new Account(200.0, "same", 1);
        from.setId(1);
        Account to = new Account(50.0, "same", 1);
        to.setId(2);

        when(repo.findById(1)).thenReturn(Optional.of(from));
        when(repo.findById(2)).thenReturn(Optional.of(to));
        when(userRepo.findById(from.getOwnerId())).thenReturn(Optional.of(new User("u","U",20,"M", null)));

        accountService.moneyTransaction(1, 2, 100.0);

        assertEquals(100.0, from.getBalance());
        assertEquals(150.0, to.getBalance());
        verify(repo, times(2)).save(any(Account.class));
        verify(producer, times(2)).sendAccountEvent(anyString(), any(AccountDTO.class));
    }

    @Test
    void moneyTransaction_friend_and_nonFriend_and_errors() {
        // friend case
        User sender = new User("s","Sender",30,"M", null);
        User friend = new User("f","Friend",25,"F", null);
        // sender friend with login 'f'
        sender.addFriend(friend);

        Account from = new Account(200.0, "s", 10);
        from.setId(10);
        Account to = new Account(20.0, "f", 11);
        to.setId(11);

        when(repo.findById(10)).thenReturn(Optional.of(from));
        when(repo.findById(11)).thenReturn(Optional.of(to));
        when(userRepo.findById(from.getOwnerId())).thenReturn(Optional.of(sender));

        accountService.moneyTransaction(10, 11, 50.0);
        // since friend, commission 3% should be applied to from account
        double perc3 = accountService.calculateCommission(50.0, 3);
        assertEquals(200.0 - 50.0 - perc3, from.getBalance());
        assertEquals(20.0 + 50.0, to.getBalance());

        // non-friend case: commission 10%
        User sender2 = new User("x","X",40,"M", null);
        Account from2 = new Account(300.0, "x", 20);
        from2.setId(20);
        Account to2 = new Account(10.0, "y", 21);
        to2.setId(21);
        when(repo.findById(20)).thenReturn(Optional.of(from2));
        when(repo.findById(21)).thenReturn(Optional.of(to2));
        when(userRepo.findById(from2.getOwnerId())).thenReturn(Optional.of(sender2));

        accountService.moneyTransaction(20, 21, 100.0);
        double perc10 = accountService.calculateCommission(100.0, 10);
        assertEquals(300.0 - 100.0 - perc10, from2.getBalance());
        assertEquals(10.0 + 100.0, to2.getBalance());

        // insufficient funds
        Account poor = new Account(10.0, "p", 30);
        poor.setId(30);
        Account other = new Account(0.0, "o", 31);
        other.setId(31);
        when(repo.findById(30)).thenReturn(Optional.of(poor));
        when(repo.findById(31)).thenReturn(Optional.of(other));
        when(userRepo.findById(poor.getOwnerId())).thenReturn(Optional.of(new User("p","P",20,"M", null)));

        assertThrows(InsufficientFundsException.class, () -> accountService.moneyTransaction(30, 31, 50.0));
    }

    @Test
    void getOperationByTypeAndId_and_getAllAccounts() {
        Account acc = new Account(100.0, "l", 1);
        acc.setId(5);
        acc.addOperation(new Operation("Put", 10.0, acc));
        acc.addOperation(new Operation("Withdraw", 5.0, acc));

        when(repo.findById(5)).thenReturn(Optional.of(acc));
        List<OperationDTO> puts = accountService.getOperationByTypeAndId(5, "Put");
        assertEquals(1, puts.size());

        when(repo.findAll()).thenReturn(List.of(acc));
        List<AccountDTO> all = accountService.getAllAccounts();
        assertEquals(1, all.size());
    }
}
