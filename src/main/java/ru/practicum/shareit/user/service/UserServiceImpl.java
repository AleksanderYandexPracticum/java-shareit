package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.storage.InMemoryItemStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service("UserServiceImpl")
public class UserServiceImpl implements UserService {
    private final InMemoryUserStorage inMemoryUserStorage;
    private final InMemoryItemStorage inMemoryItemStorage;

    @Autowired
    public UserServiceImpl(@Qualifier("InMemoryUserStorage") UserStorage userStorage,
                           @Qualifier("InMemoryItemStorage") ItemStorage itemStorage) {
        this.inMemoryUserStorage = (InMemoryUserStorage) userStorage;
        this.inMemoryItemStorage = (InMemoryItemStorage) itemStorage;
    }

    public InMemoryUserStorage getInMemoryUserStorage() {
        return inMemoryUserStorage;
    }

    private void validationUser(UserDto userDto) {
        if (userDto.getName() == null || userDto.getName().isBlank()) {
            log.info("Имя пользователя не может быть пустым");
            throw new ValidationException("Имя пользователя не может быть пустым");
        }
    }

    private void validationEmail(UserDto userDto) {
        if (userDto.getEmail().isBlank() || !userDto.getEmail().contains("@")) {
            log.info("Электронная почта не может быть пустой и должна содержать символ @");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        boolean findUser = inMemoryUserStorage.getListUsers().values()
                .stream()
                .anyMatch(user -> userDto.getEmail().equals(user.getEmail()));
        if (findUser) {
            log.info("Дубликат электронного адресса пользователя");
            throw new DuplicateEmailException("Дубликат электронного адресса пользователя");
        }
    }

    private void validationEmailNull(UserDto userDto) {
        if (userDto.getEmail() == null) {
            log.info("Электронная почта не может быть null");
            throw new ValidationException("Электронная почта не может быть null");
        }
    }

    @Override
    public UserDto add(UserDto userDto) {
        validationUser(userDto);
        validationEmailNull(userDto);
        validationEmail(userDto);
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(inMemoryUserStorage.add(user));
    }

    @Override
    public UserDto get(Long id) {
        return UserMapper.toUserDto(inMemoryUserStorage.get(id));
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(inMemoryUserStorage.update(id, user));
    }

    @Override
    public void delete(Long id) {
        inMemoryItemStorage.getListItems().remove(id);  // удаляю пользователя с вещами
        inMemoryUserStorage.delete(id);
    }

    @Override
    public List<UserDto> getAll() {
        return inMemoryUserStorage.getAll().stream()
                .map((user) -> UserMapper.toUserDto(user))
                .collect(Collectors.toList());
    }
}
