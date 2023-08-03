package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("InMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private Map<Long, User> listUsers = new HashMap<>();
    private static Long id = 1L;

    public Map<Long, User> getListUsers() {
        return listUsers;
    }


    @Override
    public User add(User user) {
        user.setId(id);
        listUsers.put(id, user);
        log.info("Добавлен пользователь № " + user.getId() + "  name = " + user.getName() + "  Email= " + user.getEmail());
        id++;
        return user;
    }

    @Override
    public User get(Long id) {
        if (listUsers.containsKey(id)) {
            return listUsers.get(id);
        }
        throw new NotFoundException(String.format("Нет такого идентификатора № %s", id));
    }

    @Override
    public User update(Long id, User user) {
        user.setId(id);
        for (User oldUser : listUsers.values()) {
            String email = oldUser.getEmail();
            Long idUser = oldUser.getId();
            if (email.equals(user.getEmail()) && !idUser.equals(id)) {
                throw new DuplicateEmailException(
                        String.format("Такой email  %s уже есть у другого пользователя № %s ", user.getEmail(), idUser));
            }
        }
        user.setName(user.getName() == null ? listUsers.get(id).getName() : user.getName());
        user.setEmail(user.getEmail() == null ? listUsers.get(id).getEmail() : user.getEmail());
        listUsers.put(id, user);
        return user;
    }

    @Override
    public void delete(Long id) {
        if (listUsers.containsKey(id)) {
            listUsers.remove(id);
        } else {
            throw new NotFoundException(String.format("Нет такого идентификатора № %s", id));
        }
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(listUsers.values());
    }
}
