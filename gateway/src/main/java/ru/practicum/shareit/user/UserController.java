package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;


@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private static final String OWNER_ID_HEADER = "X-Sharer-User-Id";
    private final UserClient userClient;


    @PostMapping
    public ResponseEntity<Object> addUser(
            @RequestBody UserDto userDto) {
        validateUser(userDto);
        validateEmailNull(userDto);
        validateEmail(userDto);
        log.info("Creating user {}, user={}, email={}", userDto.getName(), userDto.getEmail());
        return userClient.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(
            @PathVariable("userId") long userId,
            @RequestBody UserDto userDto) {
        validateEmailForUpdate(userDto);
        log.info("Update userId={}, user={}, email={}", userId, userDto.getName(), userDto.getEmail());
        return userClient.updateUser(userId, userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(
            @PathVariable("userId") long userId) {
        log.info("Get userId={}", userId);
        return userClient.getUser(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(
            @PathVariable("userId") long userId) {
        log.info("Delete userId={}", userId);
        return userClient.deleteUser(userId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAll() {
        log.info("Get ALL users");
        return userClient.getAll();
    }

    private void validateUser(UserDto userDto) {
        if (userDto.getName() == null || userDto.getName().isBlank()) {
            log.info("The user name cannot be empty");
            throw new ValidationException("The user name cannot be empty");
        }
    }

    private void validateEmailNull(UserDto userDto) {
        if (userDto.getEmail() == null) {
            log.info("Email cannot be null");
            throw new ValidationException("Email cannot be null");
        }
    }

    private void validateEmail(UserDto userDto) {
        if (userDto.getEmail().isBlank() || !userDto.getEmail().contains("@")) {
            log.info("The email cannot be empty and must contain the character @");
            throw new ValidationException("The email cannot be empty and must contain the character @");
        }
    }

    private void validateEmailForUpdate(UserDto userDto) {
        if (userDto.getEmail() != null && !userDto.getEmail().contains("@")) {
            log.info("The email cannot be empty and must contain the character @");
            throw new ValidationException("The email cannot be empty and must contain the character @");
        }
    }
}
