package com.bank.app.data.entities;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Класс друзей (используется для хранения друзей пользователя)
 */
@Data
@Entity
@Table(name = "friends")
public class Friends {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "friend_id", nullable = false)
    private User friend;

    public Friends() {}

    /**
     * Конструктор
     * @param user текущий пользователь
     * @param friend добавляемый друг
     */
    public Friends(User user, User friend) {
        this.user = user;
        this.friend = friend;
    }
}
