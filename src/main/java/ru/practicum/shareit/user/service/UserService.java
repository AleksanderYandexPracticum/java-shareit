package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto add(UserDto userDto);

    UserDto get(Long id);

    UserDto update(Long id, UserDto userDto);

    void delete(Long id);

    List<UserDto> getAll();
}
