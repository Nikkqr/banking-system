package com.bank.app.application.dto;

import com.bank.app.data.entities.HairColors;
import lombok.Data;

/**
 * дто для пользователя
 */
@Data
public class UserDTO {

    private int id;

    private String login;

    private String name;

    private int age;

    private String gender;

    private String hairColor;

    public UserDTO() {}

    public UserDTO(int id, String login, String name, int age, String gender, HairColors hairColor) {
        this.id = id;
        this.login = login;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.hairColor = hairColor.toString();
    }
}
