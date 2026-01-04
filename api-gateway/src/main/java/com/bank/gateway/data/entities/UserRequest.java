package com.bank.gateway.data.entities;

import lombok.Data;

@Data
public class UserRequest {

    private String login;

    private String name;

    private int age;

    private String gender;

    private HairColor hairColor;

    public UserRequest(String login, String name, int age, String gender, HairColor hairColor) {
        this.login = login;
        this.name = name;
        this.age = age;
        this.gender = gender;
    }
}
