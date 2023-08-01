package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component("InMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private HashMap<Long, User> listUsers = new HashMap<>();
    private static Long id = 1L;

    public HashMap<Long, User> getListUsers() {
        return listUsers;
    }


    @Override
    public UserDto add(UserDto userDto) {
        User user = UserMapper.toUser(id, userDto);
        listUsers.put(id, user);
        userDto.setId(id);
        id++;
        return userDto;
    }

    @Override
    public UserDto get(Long id) {
        if (listUsers.containsKey(id)) {
            return UserMapper.toUserDto(listUsers.get(id));
        }
        throw new NotFoundException(String.format("Нет такого идентификатора № %s", id));
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        for (User user : listUsers.values()) {
            String email = user.getEmail();
            Long idUser = user.getId();
            if (email.equals(userDto.getEmail()) && !idUser.equals(id)) {
                throw new RuntimeException(String.format("Такой email  %s уже есть у другого пользователя ", userDto.getEmail()));
            }
        }
        User user = UserMapper.toUser(id, userDto);
        user.setName(userDto.getName() == null ? listUsers.get(id).getName() : userDto.getName());
        user.setEmail(userDto.getEmail() == null ? listUsers.get(id).getEmail() : userDto.getEmail());
        listUsers.put(id, user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto delete(Long id) {
        if (listUsers.containsKey(id)) {
            UserDto userDto = UserMapper.toUserDto(listUsers.get(id));
            listUsers.remove(id);
            return userDto;
        }
        throw new NotFoundException(String.format("Нет такого идентификатора № %s", id));
    }

    @Override
    public List<UserDto> getAll() {
        return listUsers.values().stream().map((user) -> UserMapper.toUserDto(user)).collect(Collectors.toList());
    }
}
