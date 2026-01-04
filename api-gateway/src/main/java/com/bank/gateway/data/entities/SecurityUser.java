package com.bank.gateway.data.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "security_users")
public class SecurityUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public SecurityUser() {}

    public SecurityUser(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
}