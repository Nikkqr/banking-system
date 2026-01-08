package com.bank.app.presentation.controllers;

import com.bank.app.application.dto.*;
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
        UserDTO result = userService.createUser(
                request.getLogin(),
                request.getName(),
                request.getAge(),
                request.getGender(),
                request.getHairColor()
        );

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Получить информацию о пользователе по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация о пользователе найдена"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserInfo(@PathVariable int id) {
         UserDTO user = userService.userInformation(id);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Добавить друга пользователю")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Друг успешно добавлен"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PutMapping("/{id1}/addFriend")
    public ResponseEntity<Void> addFriendForUser(@PathVariable int id1, @RequestBody  int id2) {
        userService.addFriendForUser(id1, id2);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Удалить друга у пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Друг успешно удалён"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @DeleteMapping("/{id1}/deleteFriend")
    public ResponseEntity<Void> deleteUserFriend(@PathVariable int id1, @RequestBody int id2) {
        userService.deleteUserFriend(id1, id2);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Поиск друзей по айди")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Друг успешно найден"),
            @ApiResponse(responseCode = "404", description = "Друг не найден")
    })
    @GetMapping("/{userId}/friends")
    public ResponseEntity<List<UserDTO>> getFriends(@PathVariable int userId) {
        List<UserDTO> users = userService.getFriends(userId);
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Вывод пользователей отфильтрованных по цвету волос или полу")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователи успешно найдены"),
            @ApiResponse(responseCode = "404", description = "Пользователи не найдены")
    })
    @GetMapping("/filter")
    public ResponseEntity<List<UserDTO>> filterUsers(@RequestParam(required = false) HairColorsDTO hairColor, @RequestParam(required = false) String gender) {
        List<UserDTO> users = userService.getUsersByHairColorAndGender(hairColor, gender);
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(users);
    }
}
