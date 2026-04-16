package com.bank.app.application.services;

import com.bank.app.application.dto.HairColorsDTO;
import com.bank.app.application.dto.UserDTO;
import com.bank.app.application.exception.UserAlreadyExistsException;
import com.bank.app.application.exception.UserNotFoundException;
import com.bank.app.application.services.ProducerService;
import com.bank.app.application.services.UserService;
import com.bank.app.data.entities.HairColors;
import com.bank.app.data.entities.User;
import com.bank.app.data.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private ProducerService producer;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepo, producer);
    }

    @Test
    void createUser_success() {
        when(userRepo.existsByLogin("john")).thenReturn(false);
        when(userRepo.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            User saved = new User(u.getLogin(), u.getName(), u.getAge(), u.getGender(), u.getHairColor());
            saved.setId(1);
            return saved;
        });

        UserDTO dto = userService.createUser("john", "John", 30, "M", HairColors.BLACK);

        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("john", dto.getLogin());

        verify(userRepo, times(1)).save(any(User.class));
        verify(producer, times(1)).sendUserEvent(anyString(), any(UserDTO.class));
    }

    @Test
    void createUser_alreadyExists_throws() {
        when(userRepo.existsByLogin("john")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () ->
                userService.createUser("john", "John", 30, "M", HairColors.BLACK)
        );

        verify(userRepo, never()).save(any());
        verify(producer, never()).sendUserEvent(anyString(), any());
    }

    @Test
    void userInformation_success() {
        User user = new User("a", "Alice", 25, "F", HairColors.WHITE);
        user.setId(5);
        when(userRepo.findById(5)).thenReturn(Optional.of(user));

        UserDTO dto = userService.userInformation(5);

        assertNotNull(dto);
        assertEquals(5, dto.getId());
        assertEquals("a", dto.getLogin());
    }

    @Test
    void userInformation_notFound_throws() {
        when(userRepo.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.userInformation(99));
    }

    @Test
    void addAndDeleteFriend_flow() {
        User u1 = new User("u1", "User1", 20, "M", HairColors.BLACK);
        u1.setId(1);
        User u2 = new User("u2", "User2", 22, "F", HairColors.WHITE);
        u2.setId(2);

        when(userRepo.findById(1)).thenReturn(Optional.of(u1));
        when(userRepo.findById(2)).thenReturn(Optional.of(u2));

        // add friend
        userService.addFriendForUser(1, 2);
        assertTrue(u1.isFriend("u2"));
        verify(producer, times(1)).sendUserEvent(eq(String.valueOf(u1.getId())), any(UserDTO.class));

        // delete friend
        userService.deleteUserFriend(1, 2);
        assertFalse(u1.isFriend("u2"));
        verify(producer, times(2)).sendUserEvent(eq(String.valueOf(u1.getId())), any(UserDTO.class));
    }

    @Test
    void getFriends_returnsFriendDTOs() {
        User u1 = new User("u1", "User1", 20, "M", HairColors.BLACK);
        u1.setId(1);
        User u2 = new User("u2", "User2", 22, "F", HairColors.WHITE);
        u2.setId(2);

        u1.addFriend(u2);

        when(userRepo.findById(1)).thenReturn(Optional.of(u1));

        List<UserDTO> friends = userService.getFriends(1);

        assertEquals(1, friends.size());
        assertEquals("u2", friends.get(0).getLogin());
    }

    @Test
    void getUsersByHairColorAndGender_filtersCorrectly() {
        User mBlack = new User("m1", "M1", 30, "M", HairColors.BLACK);
        mBlack.setId(1);
        User fWhite = new User("f1", "F1", 28, "F", HairColors.WHITE);
        fWhite.setId(2);
        User mWhite = new User("m2", "M2", 40, "M", HairColors.WHITE);
        mWhite.setId(3);

        when(userRepo.findAll()).thenReturn(List.of(mBlack, fWhite, mWhite));

        var res1 = userService.getUsersByHairColorAndGender(HairColorsDTO.BLACK, "M");
        assertEquals(1, res1.size());
        assertEquals("m1", res1.get(0).getLogin());

        var res2 = userService.getUsersByHairColorAndGender(null, null);
        assertEquals(3, res2.size());
    }
}

