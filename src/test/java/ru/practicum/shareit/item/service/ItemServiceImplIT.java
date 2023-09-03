package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceImplIT {

    private final EntityManager em;
    private final ItemServiceImpl itemServiceImpl;
    private final UserServiceImpl userServiceImpl;

    @Test
    void add() {
        UserDto userDto = new UserDto(null, "Jon", "jon@mail.ru");
        userServiceImpl.add(userDto);

        ItemDto itemDto = ItemDto.builder()
                .name("молоток")
                .description("маленький")
                .available(true)
                .build();
        itemServiceImpl.add(1L, itemDto);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", itemDto.getName()).getSingleResult();
        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));

        assertThrows(NotFoundException.class, () -> itemServiceImpl.add(2L, itemDto));

    }

    @Test
    void get() {
    }

    @Test
    void update() {
    }

    @Test
    void getAllItemToUser() {
    }

    @Test
    void getAllItemWithText() {
    }

    @Test
    void addComment() {
    }
}