package com.bank.app.application.services;

import com.bank.app.application.dto.UserDTOCreator;
import com.bank.app.data.entities.Friends;
import com.bank.app.data.entities.HairColors;
import com.bank.app.data.entities.User;
import com.bank.app.data.repository.UserRepository;
import com.bank.app.data.resultType.Result;
import com.bank.app.data.resultType.ResultDescription;
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
    public Result createUser(String login, String name, int age, String gender, HairColors hairColor)
    {
        User user = new User(login, name, age, gender, hairColor);
        userRepo.createUser(user);
        producer.sendUserEvent(String.valueOf(user.getId()), UserDTOCreator.toDTO(user));
        return new Result(ResultDescription.TheUserHasBeenSuccessfullyCreated, user);

    }

    /**
     * Метод для просмотра информации о пользователе
     * @param id ид
     * @return результат операции
     */
    public Result userInformation(int id)
    {
        User user = userRepo.getUserById(id).getUser();
        return new Result(ResultDescription.InformationOfTheUseSuccessfullyPrinted, user);
    }

    /**
     * Метод для добавления пользователя в друзья
     * @param id1 ид основного пользователя
     * @param id2 ид добавляемого пользователя
     * @return результат операции
     */
    @Transactional
    public Result addFriendForUser(int id1, int id2)
    {
        User user1 = userRepo.getUserById(id1).getUser();
        User user2 = userRepo.getUserById(id2).getUser();

        user1.addFriend(user2);
        producer.sendUserEvent(String.valueOf(user1.getId()), UserDTOCreator.toDTO(user1));
        return new Result(ResultDescription.Success);
    }

    /**
     * Метод для удаления пользователя из друзей
     * @param id1 ид основного пользователя
     * @param id2 ид удаляемого пользователя
     * @return результат операции
     */
    @Transactional
    public Result deleteUserFriend(int id1, int id2)
    {
        User user1 = userRepo.getUserById(id1).getUser();
        User user2 = userRepo.getUserById(id2).getUser();

        user1.removeFriend(user2);
        producer.sendUserEvent(String.valueOf(user1.getId()), UserDTOCreator.toDTO(user1));
        return new Result(ResultDescription.Success);
    }

    /**
     * Метод для получения конкретного друга по ид
     * @param id ид пользователя
     * @return найденный друг
     */
    public List<User> getFriends(int id) {
        Result userResult = userRepo.getUserById(id);
        User user = userResult.getUser();
        List<Friends> userFriends = user.getFriends();
        List<User> friends = new ArrayList<>();

        for (Friends fr : userFriends) {
            friends.add(fr.getFriend());
        }

        return friends;

    }

    /**
     * Метод для получения пользователей отфильтрованных по цвету волос и полу
     * @param hairColor цвет волос
     * @param gender пол
     * @return список пользователей
     */
    public List<User> getUsersByHairColorAndGender(HairColors hairColor, String gender) {
        return userRepo.findAll().stream()
                .filter(u -> (hairColor == null || u.getHairColor().equals(hairColor)))
                .filter(u -> (gender == null || u.getGender().equals(gender)))
                .toList();

    }
}
