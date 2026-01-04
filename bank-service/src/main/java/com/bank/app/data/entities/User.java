package com.bank.app.data.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * класс пользователя
 */
@Data
@Entity
@Table(name = "users")
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    private String login;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int age;

    @Column(nullable = false)
    private String gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HairColors hairColor;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Friends> friends;

    public User() {}

    /**
     * Конструктор для пользователя
     * @param login логин
     * @param name имя
     * @param age возраст
     * @param gender пол
     * @param hairColor цвет волос
     */
    public User(String login, String name, int age, String gender, HairColors hairColor)
    {
        this.login = login;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.hairColor = hairColor;
        friends = new ArrayList<>();
    }

    /**
     * Метод проверяет, является ли пользователь другом
     * @param login - логин проверяемого
     * @return возвращает 1 если человек друг, а 0 если нет
     */
    public boolean isFriend(String login)
    {
        return friends.stream()
                .anyMatch(u -> u.getFriend().getLogin().equals(login));
    }

    /**
     * Метод для добавления пользователя в друзья
     * @param friend добавляемый в друзья пользователь
     */
    public void addFriend(User friend)
    {
        Friends fr = new Friends(this, friend);
        friends.add(fr);
    }

    /**
     * Метод для удаления пользователя из друзей
     * @param friend удаляемый друг
     */
    public void removeFriend(User friend)
    {
        friends.removeIf(f -> f.getFriend().equals(friend));
    }


    /**
     * Метод для перевода информации о пользователе в строку (для вывода)
     * @return возвращает строку содержащую данные о пользователе
     */
    public String toString() {
        return "User(" +
                "login:" + login +
                ", name:" + name +
                ", age:" + age +
                ", gender:" + gender +
                ", hairColor:" + hairColor +
                ")";
    }
}
