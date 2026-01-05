package com.bank.app.application.dto;

import com.bank.app.data.entities.HairColors;
import com.bank.app.data.entities.User;
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

    public UserDTO(User user) {
        id = user.getId();
        login = user.getLogin();
        name = user.getName();
        age = user.getAge();
        gender = user.getGender();
        hairColor = user.getHairColor().toString();
    }
}
