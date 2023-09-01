package ru.practicum.shareit.user;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private UserController userController;


    @Test
    void add_withInvoked_thenResponseStatusOkWithUserDtoInBody() {
        UserDto user = new UserDto(null, null, null);
        when(userService.add(user)).thenReturn(user);

        UserDto actualUserDto = userController.add(request, user);

        assertEquals(user, actualUserDto);

    }

    @Test
    void update() {
        UserDto user = new UserDto(null, null, null);
        when(userService.update(1L, user)).thenReturn(user);

        UserDto actualUserDto = userController.update(request, 1L, user);

        assertEquals(user, actualUserDto);
    }

    @Test
    void get() {
        UserDto user = new UserDto(null, null, null);
        when(userService.get(anyLong())).thenReturn(user);

        UserDto actualUserDto = userController.getUser(request, 1L);

        assertEquals(user, actualUserDto);
    }

    @Test
    void delete() {
        userController.deleteUser(request, 1L);

        verify(userService, Mockito.times(1)).delete(anyLong());
    }

    @Test
    void getAll() {
        List<UserDto> list = new ArrayList<>();
        when(userService.getAll()).thenReturn(list);

        List<UserDto> actualList = userController.getAll(request);
        assertEquals(list, actualList);
    }
}