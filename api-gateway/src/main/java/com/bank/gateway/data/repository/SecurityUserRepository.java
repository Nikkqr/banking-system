package com.bank.gateway.data.repository;

import com.bank.gateway.data.entities.SecurityUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SecurityUserRepository extends JpaRepository<SecurityUser, Integer> {

    default void createUser(SecurityUser user) {
        save(user);
    }

    Optional<SecurityUser> findByUsername(String username);

    default void updateUser(SecurityUser user) {
        save(user);
    }

    default void deleteUser(SecurityUser user) {
        delete(user);
    }
}
