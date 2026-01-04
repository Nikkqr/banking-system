package com.bank.app.data.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс счёта
 */
@Data
@Entity
@Table(name = "accounts")
public class Account
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private Double balance;

    @Column(nullable = false)
    private String login;

    @Column(nullable = false)
    private int ownerId;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Operation> operations;

    public Account() {}
    /**
     * Коснтруктор
     * @param balance - баланс счёта
     * @param login - логин владельца счёта
     */
    public Account(Double balance, String login, int ownerId)
    {
        this.balance = balance;
        this.login = login;
        this.ownerId = ownerId;
        operations = new ArrayList<>();
    }

    public List<Operation> getOperationHistory()
    {
        return operations;
    }

    public void setOperationHistory(List<Operation> operationHistory)
    {
        this.operations = operationHistory;
    }

    /**
     * Добавляет транзакцию в историю транзакций
     * @param operation - транзакция
     */
    public void addOperation(Operation operation)
    {
        operations.add(operation);
    }
}
