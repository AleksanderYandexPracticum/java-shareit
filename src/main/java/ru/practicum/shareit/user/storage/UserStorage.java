package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    User add(User user);

    User get(Long id);

    User update(Long id, User user);

    void delete(Long id);

    List<User> getAll();
}
