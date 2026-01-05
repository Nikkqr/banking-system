package com.bank.gateway.application.services;

import com.bank.gateway.application.dto.AccountDTO;
import com.bank.gateway.application.dto.SecUserRequest;
import com.bank.gateway.data.entities.*;
import com.bank.gateway.data.repository.SecurityUserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class SecUserService implements UserClient {

    private final SecurityUserRepository userRepository;

    private final RestTemplate restTemplate;

    private final PasswordEncoder passwordEncoder;

    public SecUserService(SecurityUserRepository userRepository, RestTemplate restTemplate, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
        passwordEncoder = encoder;
    }

    public ResponseEntity<String> createUser(SecUserRequest userRequest) {
        SecurityUser securityUser = new SecurityUser(userRequest.getName(), passwordEncoder.encode(userRequest.getPassword()),
                userRequest.getRole());
        userRepository.createUser(securityUser);
        String url = "http://localhost:8081/users";
        UserRequest uReq = new UserRequest(userRequest);
        ResponseEntity<String> response = restTemplate.postForEntity(url, uReq, String.class);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    public int getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        return userRepository.findByUsername(userName).get().getId();
    }

    public ResponseEntity<String> getFilterUsers(HairColor hairColor, String gender){
        List<String> params = new ArrayList<>();
        if (hairColor != null){
            params.add("hairColor=" + hairColor.name());
        }
        if (gender != null){
            params.add("gender=" + gender);
        }

        String query = params.isEmpty() ? "" : "?" + String.join("&", params);
        String url = "http://localhost:8081/users/filter" + query;

        return restTemplate.getForEntity(url, String.class);
    }

    public ResponseEntity<String> getUserInfo(int id){
        String url = "http://localhost:8081/users/{id}";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class, id);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    public ResponseEntity<String> getAccounts(){
        String url = "http://localhost:8081/accounts/allAccounts";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    public ResponseEntity<List<AccountDTO>> getAccounts(int id){
        String url = "http://localhost:8081/accounts/allAccounts";

        ResponseEntity<AccountDTO[]> response = restTemplate.getForEntity(url, AccountDTO[].class);

        List<AccountDTO> accounts = Arrays.stream(response.getBody())
                .filter(account -> account.getOwnerId() == id)
                .toList();

        return ResponseEntity.status(response.getStatusCode()).body(accounts);
    }

    public ResponseEntity<String> accountById(int id, String type, String account){
        String url = "http://localhost:8081/accounts/{id}/operation?type={type}";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class, id, type);
        return ResponseEntity.status(response.getStatusCode()).body(account + " " + response.getBody());
    }
}
