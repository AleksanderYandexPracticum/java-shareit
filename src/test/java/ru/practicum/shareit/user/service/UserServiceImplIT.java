package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceImplIT {

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

    }

    @Test
    void get() {
        UserDto userDto = new UserDto(null, "Jon", "jon@mail.ru");
        userServiceImpl.add(userDto);

        UserDto returnUserDto = userServiceImpl.get(1L);

        assertThat(returnUserDto.getId(), notNullValue());
        assertThat(returnUserDto.getName(), equalTo(userDto.getName()));
        assertThat(returnUserDto.getEmail(), equalTo(userDto.getEmail()));

    }

    @Test
    void update() {
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
    }
}