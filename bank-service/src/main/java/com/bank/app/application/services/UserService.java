package com.bank.app.application.services;

import com.bank.app.application.dto.HairColorsDTO;
import com.bank.app.application.dto.UserDTO;
import com.bank.app.data.entities.HairColors;

import java.util.List;

public interface UserService {
	UserDTO createUser(String login, String name, int age, String gender, HairColors hairColor);

	UserDTO userInformation(int id);

	void addFriendForUser(int id1, int id2);

	void deleteUserFriend(int id1, int id2);

	List<UserDTO> getFriends(int id);

	List<UserDTO> getUsersByHairColorAndGender(HairColorsDTO hairColorDTO, String gender);
}
