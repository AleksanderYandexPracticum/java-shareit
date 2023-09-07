package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityNotFoundException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userServiceImp;

    @Test
    void add() {
        User user = new User("Jon", "jon@mail.ru");
        when(userRepository.save(user)).thenReturn(user);

        UserDto userDto = new UserDto(null, "Jon", "jon@mail.ru");
        UserDto actualUserDto = userServiceImp.add(userDto);

        assertEquals(user.getName(), actualUserDto.getName());
        assertEquals(user.getEmail(), actualUserDto.getEmail());
        assertEquals(UserMapper.toUserDto(user), actualUserDto);

        UserDto user1 = new UserDto(null, null, "jon@mail.ru");
        assertThrows(ValidationException.class, () -> userServiceImp.add(user1));

        UserDto user2 = new UserDto(null, "Jon", null);
        assertThrows(ValidationException.class, () -> userServiceImp.add(user2));

        UserDto user3 = new UserDto(null, "Jon", "jonmail.ru");
        assertThrows(ValidationException.class, () -> userServiceImp.add(user3));
    }

    @Test
    void get() {
        User user = new User("Jon", "jon@mail.ru");
        when(userRepository.getById(1L)).thenReturn(user);

        UserDto actualUserDto = userServiceImp.get(1L);

        assertEquals(user.getName(), actualUserDto.getName());
        assertEquals(user.getEmail(), actualUserDto.getEmail());
        assertEquals(UserMapper.toUserDto(user), actualUserDto);

        when(userRepository.getById(1L)).thenThrow(new EntityNotFoundException("There is no user with an ID  № 1"));
        assertThrows(NotFoundException.class, () -> userServiceImp.get(1L));
    }

    @Test
    void update() {
        UserDto userDto = new UserDto(null, "Jon", "jon@mail.ru");
        Long id = 1L;

        User user = new User("Jon", "jon@mail.ru");
        when(userRepository.getById(id)).thenReturn(user);

        user.setId(id);
        when(userRepository.save(user)).thenReturn(user);

        UserDto actualUserDto = userServiceImp.update(id, userDto);

        assertEquals(user.getName(), actualUserDto.getName());
        assertEquals(user.getEmail(), actualUserDto.getEmail());
        assertEquals(UserMapper.toUserDto(user), actualUserDto);

        UserDto user1 = new UserDto(null, "Jon", "jonmail.ru");
        assertThrows(ValidationException.class, () -> userServiceImp.update(id, user1));

    }

    @Test
    void delete() {
        Long id = 1L;

        userServiceImp.delete(id);

        verify(userRepository, Mockito.times(1)).removeUserById(id);
    }

    @Test
    void getAll() {
        List<User> list = new ArrayList<>();
        when(userRepository.findAll()).thenReturn(list);

        List<UserDto> actualListUserDto = userServiceImp.getAll();

        assertEquals(list, actualListUserDto);
    }
}