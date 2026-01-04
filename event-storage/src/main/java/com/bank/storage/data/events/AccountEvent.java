package com.bank.storage.data.events;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class AccountEvent
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String entityKey;

    @Column(nullable = false)
    private String eventData;

    public AccountEvent() {}

    public AccountEvent(String entityKey, String eventData) {
        this.entityKey = entityKey;
        this.eventData = eventData;
    }
}
