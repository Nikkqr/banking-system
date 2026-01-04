package com.bank.app.application.dto;

import com.bank.app.data.entities.HairColors;
import lombok.Data;

@Data
public class CreateUserRequest {

    private String login;

    private String name;

    private int age;

    private String gender;

    private HairColors hairColor;
}
