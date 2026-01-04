package com.bank.app.data.resultType;

import com.bank.app.data.entities.Account;
import com.bank.app.data.entities.User;

/**
 * класс используемый для возврата результатов различных методов и проброски объетков
 * пользователя и счёта
 */
public class Result
{
    private final ResultDescription status;

    private double information;

    private User user;

    private Account account;

    /**
     * конструктор только с результатом
     * @param status - информация о результате
     */
    public Result(ResultDescription status)
    {
        this.status = status;
    }

    /**
     * конструктор с результатом и пользователем
     * @param status - информация о результате
     * @param user - пользователь
     */
    public Result(ResultDescription status, User user)
    {
        this.status = status;
        this.user = user;
    }

    /**
     * конструктор с результатом и счётом
     * @param status - информация о результате
     * @param account - счёт
     */
    public Result(ResultDescription status, Account account)
    {
        this.status = status;
        this.account = account;
    }

    public Result(ResultDescription status, double information)
    {
        this.status = status;
        this.information = information;
    }


    public ResultDescription getStatus()
    {
        return status;
    }

    public Account getAccount()
    {
        return account;
    }

    public User getUser()
    {
        return user;
    }

    public double getInformation()
    {
        return information;
    }
}
