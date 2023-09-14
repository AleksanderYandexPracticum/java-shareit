package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    private Long user1Id;
    private Long user2Id;
    private Long user3Id;

    @BeforeEach
    private void addUser() {
        User user1 = new User("Jon", "jon@mail.ru");
        User user2 = new User("Piter", "pit@yandex.ru");
        User user3 = new User("Magnus", "mag@rambler.ru");
        User returnUser1 = userRepository.save(user1);
        User returnUser2 = userRepository.save(user2);
        User returnUser3 = userRepository.save(user3);
        this.user1Id = returnUser1.getId();
        this.user2Id = returnUser2.getId();
        this.user3Id = returnUser3.getId();
    }

    @AfterEach
    private void deleteUsers() {
        userRepository.deleteAll();
    }

    @Test
    void findAll() {
        List<User> list = userRepository.findAll();
        assertTrue(list.size() == 3);
    }

    @Test
    void save() {
        User user4 = new User("Balamut", "bal@rambler.ru");
        User user = userRepository.save(user4);
        assertEquals(user, user4);
    }

    @Test
    void getById() {
        User user = userRepository.getById(user2Id);
        assertEquals(user.getName(), "Piter");
        assertEquals(user.getEmail(), "pit@yandex.ru");
    }

    @Test
    void removeUserById() {
        userRepository.removeUserById(user2Id);
        List<User> list = userRepository.findAll();
        assertTrue(list.size() == 2);
        User user = new User("Piter", "pit@yandex.ru");
        user.setId(user2Id);
        assertFalse(list.contains(user));
    }

    @Test
    void findUserById() {
        User user = userRepository.findUserById(user3Id);
        assertEquals(user.getName(), "Magnus");
        assertEquals(user.getEmail(), "mag@rambler.ru");
    }

}