package com.bank.app.data.repository;

import com.bank.app.data.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * интерфейс для класса репозиторий пользователей
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer>{
    boolean existsByLogin(String login);
}
