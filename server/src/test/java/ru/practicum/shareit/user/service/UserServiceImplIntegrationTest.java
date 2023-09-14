package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceImplIntegrationTest {

    private final EntityManager em;
    private final UserServiceImpl userServiceImpl;

    @Test
    void add() {
        UserDto userDto = new UserDto(null, "Jon", "jon@mail.ru");
        userServiceImpl.add(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();
        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));

//        UserDto user1 = new UserDto(null, null, "jon@mail.ru");
//        assertThrows(ValidationException.class, () -> userServiceImpl.add(user1));
//
//        UserDto user2 = new UserDto(null, "Jon", null);
//        assertThrows(ValidationException.class, () -> userServiceImpl.add(user2));
//
//        UserDto user3 = new UserDto(null, "Jon", "jonmail.ru");
//        assertThrows(ValidationException.class, () -> userServiceImpl.add(user3));

        assertThrows(DuplicateEmailException.class, () -> userServiceImpl.add(userDto));

    }

    @Test
    void get() {
        UserDto userDto = new UserDto(null, "Jon", "jon@mail.ru");
        userServiceImpl.add(userDto);

        UserDto returnUserDto = userServiceImpl.get(1L);

        assertThat(returnUserDto.getId(), notNullValue());
        assertThat(returnUserDto.getName(), equalTo(userDto.getName()));
        assertThat(returnUserDto.getEmail(), equalTo(userDto.getEmail()));

        assertThrows(NotFoundException.class, () -> userServiceImpl.get(2L));
    }

    @Test
    void update() {
        UserDto userDto1 = new UserDto(null, "Jon", "jon@mail.ru");
        userServiceImpl.add(userDto1);
        UserDto userDto2 = new UserDto(null, "JonPegon", null);
        userServiceImpl.update(1L, userDto2);
        UserDto returnUserDto = userServiceImpl.get(1L);

        assertThat(returnUserDto.getId(), notNullValue());
        assertThat(returnUserDto.getName(), equalTo(userDto2.getName()));
        assertThat(returnUserDto.getEmail(), equalTo(userDto1.getEmail()));

        UserDto userDto3 = new UserDto(null, null, "jonNEW@mail.ru");
        returnUserDto = userServiceImpl.update(1L, userDto3);
        assertThat(returnUserDto.getEmail(), equalTo(userDto3.getEmail()));

//        UserDto userDto5 = new UserDto(null, null, "jonNEWmail.ru");
//        assertThrows(ValidationException.class, () -> userServiceImpl.update(1L, userDto5));
    }

    @Test
    void delete() {
        UserDto userDto1 = new UserDto(null, "Jon", "jon@mail.ru");
        userServiceImpl.add(userDto1);
        UserDto userDto2 = new UserDto(null, "Piter", "pit@mail.ru");
        userServiceImpl.add(userDto2);

        List<UserDto> list = userServiceImpl.getAll();
        assertTrue(list.size() == 2);
        userServiceImpl.delete(1L);
        userServiceImpl.delete(2L);
        list = userServiceImpl.getAll();
        assertTrue(list.size() == 0);
    }

    @Test
    void getAll() {
        UserDto userDto1 = new UserDto(null, "Jon", "jon@mail.ru");
        UserDto userDto2 = new UserDto(null, "Piter", "pit@mail.ru");
        userServiceImpl.add(userDto1);
        userServiceImpl.add(userDto2);
        List<UserDto> list = userServiceImpl.getAll();
        assertTrue(list.size() == 2);
        userServiceImpl.delete(1L);
        userServiceImpl.delete(2L);
        list = userServiceImpl.getAll();
        assertTrue(list.size() == 0);
    }
}