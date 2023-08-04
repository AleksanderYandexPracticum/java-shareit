package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserServiceImpl userServiceImpl;

    @Autowired
    public UserController(@Qualifier("UserServiceImpl") UserService userService) {
        this.userServiceImpl = (UserServiceImpl) userService;
    }

    @PostMapping
    public UserDto add(HttpServletRequest request, @RequestBody UserDto userDto) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return userServiceImpl.add(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(HttpServletRequest request, @PathVariable("userId") Long id, @RequestBody UserDto userDto) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return userServiceImpl.update(id, userDto);
    }

    @GetMapping("/{userId}")
    public UserDto get(HttpServletRequest request, @PathVariable("userId") Long id) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return userServiceImpl.get(id);
    }

    @DeleteMapping("/{userId}")
    public void delete(HttpServletRequest request, @PathVariable("userId") Long id) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        userServiceImpl.delete(id);
    }

    @GetMapping()
    public List<UserDto> getAll(HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return userServiceImpl.getAll();
    }
}
