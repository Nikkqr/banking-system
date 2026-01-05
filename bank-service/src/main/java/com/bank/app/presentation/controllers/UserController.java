package com.bank.app.presentation.controllers;

import com.bank.app.application.dto.*;
import com.bank.app.data.entities.HairColors;
import com.bank.app.data.resultType.Result;
import com.bank.app.application.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер отвечающий за операции с пользователями
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Создание нового пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно создан"),
            @ApiResponse(responseCode = "400", description = "Неверные параметры запроса")
    })
    @PostMapping("")
    public ResponseEntity<UserDTO> createUser(@RequestBody CreateUserRequest request) {
        Result res = userService.createUser(
                request.getLogin(),
                request.getName(),
                request.getAge(),
                request.getGender(),
                request.getHairColor()
        );

        return ResponseEntity.ok(new UserDTO(res.getUser()));
    }

    @Operation(summary = "Получить информацию о пользователе по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация о пользователе найдена"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/{id}")
    public String getUserInfo(@PathVariable int id) {
        return userService.userInformation(id).getUser().toString();
    }

    @Operation(summary = "Добавить друга пользователю")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Друг успешно добавлен"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PutMapping("/{id1}/addFriend")
    public void addFriendForUser(@PathVariable int id1, @RequestBody  int id2) {
        userService.addFriendForUser(id1, id2);
    }

    @Operation(summary = "Удалить друга у пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Друг успешно удалён"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @DeleteMapping("/{id1}/deleteFriend")
    public void deleteUserFriend(@PathVariable int id1, @RequestBody int id2) {
        userService.deleteUserFriend(id1, id2);
    }

    @Operation(summary = "Поиск друзей по айди")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Друг успешно найден"),
            @ApiResponse(responseCode = "404", description = "Друг не найден")
    })
    @GetMapping("/{userId}/friends")
    public List<UserDTO> getFriends(@PathVariable int userId) {
        return userService.getFriends(userId);
    }

    @Operation(summary = "Вывод пользователей отфильтрованных по цвету волос или полу")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователи успешно найдены"),
            @ApiResponse(responseCode = "404", description = "Пользователи не найдены")
    })
    @GetMapping("/filter")
    public List<UserDTO> filterUsers(@RequestParam(required = false) HairColors hairColor, @RequestParam(required = false) String gender) {
        return userService.getUsersByHairColorAndGender(hairColor, gender);
    }
}
