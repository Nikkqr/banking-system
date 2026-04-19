package com.bank.gateway.application.services;

import com.bank.gateway.application.dto.AccountDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SecAccountServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private SecAccountService accountService;

    @Test
    void getAccountById_returnsMatching() {
        AccountDTO a1 = new AccountDTO(100.0, "l1", 1);
        a1.setId(5);
        AccountDTO a2 = new AccountDTO(10.0, "l2", 2);
        a2.setId(6);

        ResponseEntity<AccountDTO[]> resp = new ResponseEntity<>(new AccountDTO[]{a1, a2}, HttpStatus.OK);
        when(restTemplate.getForEntity("http://localhost:8081/accounts/allAccounts", AccountDTO[].class))
                .thenReturn(resp);

        AccountDTO found = accountService.getAccountById(5);
        assertNotNull(found);
        assertEquals(5, found.getId());
    }

    @Test
    void getUserInfo_delegatesToRestTemplate() {
        when(userClient.getCurrentUserId()).thenReturn(7);
        ResponseEntity<String> response = ResponseEntity.ok("user-info");
        when(restTemplate.getForEntity("http://localhost:8081/users/{id}", String.class, 7)).thenReturn(response);

        ResponseEntity<String> res = accountService.getUserInfo();
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals("user-info", res.getBody());
    }

    @Test
    void getUserAccounts_filtersByOwner() {
        when(userClient.getCurrentUserId()).thenReturn(3);

        AccountDTO a1 = new AccountDTO(10.0, "l1", 3);
        a1.setId(1);
        AccountDTO a2 = new AccountDTO(20.0, "l2", 4);
        a2.setId(2);
        ResponseEntity<AccountDTO[]> resp = new ResponseEntity<>(new AccountDTO[]{a1, a2}, HttpStatus.OK);
        when(restTemplate.getForEntity("http://localhost:8081/accounts/allAccounts", AccountDTO[].class)).thenReturn(resp);

        ResponseEntity<List<AccountDTO>> res = accountService.getUserAccounts();
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(1, res.getBody().size());
        assertEquals(3, res.getBody().get(0).getOwnerId());
    }

    @Test
    void addAndRemoveFriend_callsRestTemplate() {
        when(userClient.getCurrentUserId()).thenReturn(10);

        accountService.addFriend(2);
        verify(restTemplate, times(1)).put(eq("http://localhost:8081/users/{id1}/addFriend"), eq(2), eq(10));

        accountService.removeFriend(2);
        verify(restTemplate, times(1)).delete(eq("http://localhost:8081/users/{id1}/deleteFriend"), eq(2), eq(10));
    }

    @Test
    void transfer_with_wrongOwner_forbidden_and_success() {
        when(userClient.getCurrentUserId()).thenReturn(1);
        // wrong owner
        ResponseEntity<String> res = accountService.transfer(1,2, 99, 50.0);
        assertEquals(HttpStatus.FORBIDDEN, res.getStatusCode());

        // success
        when(userClient.getCurrentUserId()).thenReturn(5);
        ResponseEntity<String> ok = accountService.transfer(1,2,5, 50.0);
        assertEquals(HttpStatus.OK, ok.getStatusCode());
    }

    @Test
    void withdraw_and_put_checksOwner() {
        when(userClient.getCurrentUserId()).thenReturn(2);
        ResponseEntity<String> res1 = accountService.withdraw(2, 10, 5.0);
        assertEquals(HttpStatus.OK, res1.getStatusCode());

        ResponseEntity<String> res2 = accountService.put(2, 10, 5.0);
        assertEquals(HttpStatus.OK, res2.getStatusCode());

        ResponseEntity<String> forbidden = accountService.withdraw(3, 10, 1.0);
        assertEquals(HttpStatus.FORBIDDEN, forbidden.getStatusCode());
    }
}
