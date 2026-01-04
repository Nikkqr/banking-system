package com.bank.storage.data.repository;

import com.bank.storage.data.events.UserEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserEventRepository extends JpaRepository<UserEvent, Integer>
{
    default void createUserEvent(UserEvent user) {
        save(user);
    }
}
