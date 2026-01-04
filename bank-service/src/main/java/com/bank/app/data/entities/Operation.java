package com.bank.app.data.entities;
import jakarta.persistence.*;
import lombok.Data;

/**
 * класс транзакции (используется для истории транзакций)
 */
@Data
@Entity
@Table(name = "operations")
public class Operation
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double moneyCount;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    public Operation() {}

    /**
     * Конструктор
     * @param name имя операции
     * @param count количество денег в операции
     * @param fromAccount счёт с которого была совершенна операция
     */
    public Operation(String name, Double count, Account fromAccount){
        this.name = name;
        moneyCount = count;
        this.account = fromAccount;
    }
}
