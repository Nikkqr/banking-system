package com.bank.app.data.repository;

import com.bank.app.data.entities.Account;
import com.bank.app.data.resultType.Result;
import com.bank.app.data.resultType.ResultDescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * интерфейс для класса репозиторий счетов
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Integer>
{
    default Result createAccount(Account account) {
        Account savedAccount = save(account);
        return new Result(ResultDescription.TheAccountIsSuccessfullyAdded, savedAccount);
    }

    default Result getAccountById(int id) {
        if (!existsById(id)) {
            return new Result(ResultDescription.TheAccountIsNotFound);
        }
        Account account = findById(id).get();
        return new Result(ResultDescription.TheAccountIsSuccessfullyFound, account);
    }

    default Result updateAccount(Account account) {
        if (!existsById(account.getId())) {
            return new Result(ResultDescription.TheAccountIsNotFound);
        }
        Account updatedAccount = save(account);
        return new Result(ResultDescription.TheAccountIsSuccessfullyUpdated, updatedAccount);
    }

    default Result deleteAccount(Account account) {
        if (!existsById(account.getId())) {
            return new Result(ResultDescription.TheAccountIsNotFound);
        }
        delete(account);
        return new Result(ResultDescription.TheAccountIsSuccessfullyDeleted);
    }
}
