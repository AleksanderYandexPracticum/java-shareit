package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserStorage {

    UserDto add(UserDto userDto);

    UserDto get(Long id);

    UserDto update(Long id, UserDto userDto);

    UserDto delete(Long id);

    List<UserDto> getAll();
}
