package com.bank.app.data.repository;

import com.bank.app.data.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * интерфейс для класса репозиторий счетов
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Integer>{
}
