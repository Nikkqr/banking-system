package com.bank.gateway.data.entities;

import com.bank.gateway.application.dto.SecUserRequest;
import lombok.Data;

@Data
public class UserRequest {

    private String login;

    private String name;

    private int age;

    private String gender;

    private HairColor hairColor;

    public UserRequest(SecUserRequest user) {
        login = user.getLogin();
        name = user.getName();
        age = user.getAge();
        gender = user.getGender();
        hairColor = user.getHairColor();
    }
}
