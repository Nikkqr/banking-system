package com.bank.gateway.application.services;

import com.bank.gateway.application.dto.AccountDTO;
import com.bank.gateway.application.dto.SecUserRequest;
import com.bank.gateway.data.entities.Role;
import com.bank.gateway.data.entities.SecurityUser;
import com.bank.gateway.data.entities.HairColor;
import com.bank.gateway.data.repository.SecurityUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SecUserServiceTest {

    @Mock
    private SecurityUserRepository userRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private PasswordEncoder passwordEncoder;

    private SecUserService service;

    private SecurityContext originalContext;

    @BeforeEach
    void setUp() {
        service = new SecUserService(userRepository, restTemplate, passwordEncoder);
        originalContext = SecurityContextHolder.getContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.setContext(originalContext);
    }

    @Test
    void createUser_savesSecurityUser_and_forwardsRequest() {
        SecUserRequest req = new SecUserRequest();
        req.setName("bob");
        req.setPassword("pass");
        req.setRole(Role.CLIENT);

        when(passwordEncoder.encode("pass")).thenReturn("encoded");
        ResponseEntity<String> remote = new ResponseEntity<>("created", HttpStatus.CREATED);
        when(restTemplate.postForEntity(eq("http://localhost:8081/users"), any(), eq(String.class))).thenReturn(remote);

        ResponseEntity<String> res = service.createUser(req);
        assertEquals(HttpStatus.CREATED, res.getStatusCode());
        assertEquals("created", res.getBody());
        verify(userRepository, times(1)).save(any(SecurityUser.class));
    }

    @Test
    void getCurrentUserId_readsFromSecurityContext_and_repo() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("bob");
        SecurityContext ctx = mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(ctx);

        SecurityUser su = new SecurityUser("bob","x", Role.CLIENT);
        su.setId(42);
        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(su));

        int id = service.getCurrentUserId();
        assertEquals(42, id);
    }

    @Test
    void getFilterUsers_buildsQueryAndDelegates() {
        when(restTemplate.getForEntity("http://localhost:8081/users/filter?hairColor=BLACK&gender=M", String.class))
                .thenReturn(ResponseEntity.ok("ok"));

        ResponseEntity<String> res = service.getFilterUsers(HairColor.BLACK, "M");
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals("ok", res.getBody());
    }

    @Test
    void getAccounts_filtersByOwnerId() {
        AccountDTO a1 = new AccountDTO(10.0, "l", 7);
        a1.setId(1);
        AccountDTO a2 = new AccountDTO(5.0, "l2", 8);
        a2.setId(2);
        ResponseEntity<AccountDTO[]> resp = new ResponseEntity<>(new AccountDTO[]{a1,a2}, HttpStatus.OK);
        when(restTemplate.getForEntity("http://localhost:8081/accounts/allAccounts", AccountDTO[].class)).thenReturn(resp);

        ResponseEntity<List<AccountDTO>> out = service.getAccounts(7);
        assertEquals(HttpStatus.OK, out.getStatusCode());
        assertEquals(1, out.getBody().size());
        assertEquals(7, out.getBody().get(0).getOwnerId());
    }

    @Test
    void accountById_appendsAccountName() {
        when(restTemplate.getForEntity("http://localhost:8081/accounts/{id}/operation?type={type}", String.class, 5, "Put"))
                .thenReturn(ResponseEntity.ok("operation-body"));

        ResponseEntity<String> res = service.accountById(5, "Put", "ACC123");
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals("ACC123 operation-body", res.getBody());
    }
}

