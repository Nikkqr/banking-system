package com.bank.gateway.data.repository;

import com.bank.gateway.data.entities.SecurityUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SecurityUserRepository extends JpaRepository<SecurityUser, Integer> {
    Optional<SecurityUser> findByUsername(String username);
}
