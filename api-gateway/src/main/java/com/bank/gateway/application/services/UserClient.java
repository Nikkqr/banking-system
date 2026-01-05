package com.bank.gateway.application.services;

import com.bank.gateway.application.dto.AccountDTO;
import com.bank.gateway.data.entities.HairColor;
import com.bank.gateway.application.dto.SecUserRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserClient {

    ResponseEntity<String> createUser(SecUserRequest userRequest);

    int getCurrentUserId();

    ResponseEntity<String> getFilterUsers(HairColor hairColor, String gender);

    ResponseEntity<String> getUserInfo(int id);

    ResponseEntity<String> getAccounts();

    ResponseEntity<List<AccountDTO>> getAccounts(int id);

    ResponseEntity<String> accountById(int id, String type, String acc);
}
