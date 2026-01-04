package com.bank.storage.data.events;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class UserEvent
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String entityKey;

    @Column(nullable = false)
    private String eventData;

    public UserEvent() {}

    public UserEvent(String entityKey, String object) {
        this.entityKey = entityKey;
        this.eventData = object;
    }

}
