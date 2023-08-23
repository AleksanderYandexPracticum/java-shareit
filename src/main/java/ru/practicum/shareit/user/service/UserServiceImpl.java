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


    @Transactional
    @Override
    public UserDto add(UserDto userDto) {
        validateUser(userDto);
        validateEmailNull(userDto);
        validateEmail(userDto);
        User user = UserMapper.toUser(userDto);
        try {
            userDto = UserMapper.toUserDto(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            log.info("Duplicate of the user's email address");
            throw new DuplicateEmailException("Duplicate of the user's email address");
        }
        return userDto;
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto get(Long id) {
        try {
            return UserMapper.toUserDto(userRepository.getById(id));
        } catch (EntityNotFoundException e) {
            log.info(String.format("There is no user with an ID  № %s", id));
            throw new NotFoundException(String.format("There is no user with an ID  № %s", id));
        }
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
            validateEmailForUpdate(id, userDto);
        }
        newUser.setId(id);

        try {
            userDto = UserMapper.toUserDto(userRepository.save(newUser));
        } catch (DataIntegrityViolationException e) {
            log.info("Duplicate of the user's email address");
            throw new DuplicateEmailException("Duplicate of the user's email address");
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

    private void validateUser(UserDto userDto) {
        if (userDto.getName() == null || userDto.getName().isBlank()) {
            log.info("The user name cannot be empty");
            throw new ValidationException("The user name cannot be empty");
        }
    }

    private void validateEmail(UserDto userDto) {
        if (userDto.getEmail().isBlank() || !userDto.getEmail().contains("@")) {
            log.info("The email cannot be empty and must contain the character @");
            throw new ValidationException("The email cannot be empty and must contain the character @");
        }
    }

    private void validateEmailForUpdate(Long id, UserDto userDto) {
        if (!userDto.getEmail().contains("@")) {
            log.info("The email cannot be empty and must contain the character @");
            throw new ValidationException("The email cannot be empty and must contain the character @");
        }
    }

    private void validateEmailNull(UserDto userDto) {
        if (userDto.getEmail() == null) {
            log.info("Email cannot be null");
            throw new ValidationException("Email cannot be null");
        }
    }
}
