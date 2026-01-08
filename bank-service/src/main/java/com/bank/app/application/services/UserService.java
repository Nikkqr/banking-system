package com.bank.app.application.services;

import com.bank.app.application.dto.HairColorsDTO;
import com.bank.app.application.dto.UserDTO;
import com.bank.app.application.exception.UserAlreadyExistsException;
import com.bank.app.application.exception.UserNotFoundException;
import com.bank.app.data.entities.Friends;
import com.bank.app.data.entities.HairColors;
import com.bank.app.data.entities.User;
import com.bank.app.data.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервисы, работающие с пользователями
 */
@Service
public class UserService
{
    private final UserRepository userRepo;

    private final ProducerService producer;

    /**
     * Конструктор
     * @param userRepository репозиторий с пользователями
     */
    public UserService(UserRepository userRepository, ProducerService producer)
    {
        userRepo = userRepository;
        this.producer = producer;
    }

    /**
     * Метод для создания
     * @param login - логин пользователя
     * @param name - имя пользователя
     * @param age - возраст пользователя
     * @param gender - пол пользователя
     * @param hairColor - цвет волос
     * @return результат
     */
    public UserDTO createUser(String login, String name, int age, String gender, HairColors hairColor)
    {
        if (userRepo.existsByLogin(login)){
            throw new UserAlreadyExistsException("User with login '" + login + "' already exists");
        }

        User user = new User(login, name, age, gender, hairColor);
        User saved = userRepo.save(user);
        producer.sendUserEvent(String.valueOf(user.getId()), new UserDTO(user));
        return new UserDTO(saved);
    }

    /**
     * Метод для просмотра информации о пользователе
     * @param id ид
     * @return результат операции
     */
    public UserDTO userInformation(int id)
    {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id=" + id + " not found"));
        return new UserDTO(user);
    }

    /**
     * Метод для добавления пользователя в друзья
     * @param id1 ид основного пользователя
     * @param id2 ид добавляемого пользователя
     */
    @Transactional
    public void addFriendForUser(int id1, int id2)
    {
        User user1 = userRepo.findById(id1)
                .orElseThrow(() -> new UserNotFoundException("User with id=" + id1 + " not found"));
        User user2 = userRepo.findById(id2)
                .orElseThrow(() -> new UserNotFoundException("User with id=" + id2 + " not found"));

        user1.addFriend(user2);
        producer.sendUserEvent(String.valueOf(user1.getId()), new UserDTO(user1));
    }

    /**
     * Метод для удаления пользователя из друзей
     * @param id1 ид основного пользователя
     * @param id2 ид удаляемого пользователя
     */
    @Transactional
    public void deleteUserFriend(int id1, int id2)
    {
        User user1 = userRepo.findById(id1)
                .orElseThrow(() -> new UserNotFoundException("User with id=" + id1 + " not found"));
        User user2 = userRepo.findById(id2)
                .orElseThrow(() -> new UserNotFoundException("User with id=" + id2 + " not found"));

        user1.removeFriend(user2);
        producer.sendUserEvent(String.valueOf(user1.getId()), new UserDTO(user1));
    }

    /**
     * Метод для получения конкретного друга по ид
     * @param id ид пользователя
     * @return найденный друг
     */
    public List<UserDTO> getFriends(int id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id=" + id + " not found"));

        return user.getFriends().stream()
                .map(Friends::getFriend)
                .map(UserDTO::new)
                .toList();
    }

    /**
     * Метод для получения пользователей отфильтрованных по цвету волос и полу
     * @param hairColorDTO цвет волос
     * @param gender пол
     * @return список пользователей
     */
    public List<UserDTO> getUsersByHairColorAndGender(HairColorsDTO hairColorDTO, String gender) {
        HairColors hairColor = (hairColorDTO != null) ? HairColorsDTO.toDomain(hairColorDTO) : null;

        return userRepo.findAll().stream()
                .filter(u -> (hairColor == null || u.getHairColor().equals(hairColor)))
                .filter(u -> (gender == null || u.getGender().equals(gender)))
                .map(UserDTO::new)
                .toList();

    }
}
