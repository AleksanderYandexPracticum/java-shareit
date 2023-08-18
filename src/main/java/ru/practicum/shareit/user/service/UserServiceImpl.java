package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service("UserServiceImpl")
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
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
    }

    private void validationEmailforUpdate(Long id, UserDto userDto) {
        if (!userDto.getEmail().contains("@")) {
            log.info("Электронная почта не может быть пустой и должна содержать символ @");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
    }

    private void validationEmailNull(UserDto userDto) {
        if (userDto.getEmail() == null) {
            log.info("Электронная почта не может быть null");
            throw new ValidationException("Электронная почта не может быть null");
        }
    }

    @Transactional
    @Override
    public UserDto add(UserDto userDto) {
        validationUser(userDto);
        validationEmailNull(userDto);
        validationEmail(userDto);
        User user = UserMapper.toUser(userDto);
        try {
            userDto = UserMapper.toUserDto(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            log.info("Дубликат электронного адреса пользователя");
            throw new DuplicateEmailException("Дубликат электронного адреса пользователя");
        }
        return userDto;
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto get(Long id) {
        UserDto userDto;
        try {
            userDto = UserMapper.toUserDto(userRepository.getById(id));
        } catch (EntityNotFoundException e) {
            log.info(String.format("Нет пользователя с идентификатором  № %s", id));
            throw new NotFoundException(String.format("Нет пользователя с идентификатором  № %s", id));
        }
        return userDto;
    }

    @Transactional
    @Override
    public UserDto update(Long id, UserDto userDto) {
        User oldUser = userRepository.getById(id);
        User newUser = UserMapper.toUser(userDto);

        if (userDto.getName() == null || userDto.getName().isBlank()) {
            newUser.setName(oldUser.getName());
        }
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            newUser.setEmail(oldUser.getEmail());
        } else {
            validationEmailforUpdate(id, userDto);
        }
        newUser.setId(id);

        try {
            userDto = UserMapper.toUserDto(userRepository.save(newUser));
        } catch (DataIntegrityViolationException e) {
            log.info("Дубликат электронного адреса пользователя");
            throw new DuplicateEmailException("Дубликат электронного адреса пользователя");
        }
        return userDto;
    }

    @Transactional
    @Override
    public void delete(Long id) {
        userRepository.removeUserById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map((user) -> UserMapper.toUserDto(user))
                .collect(Collectors.toList());
    }
}
