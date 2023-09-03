package ru.practicum.shareit.request.Service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RequestServiceImplIT {

    private final EntityManager em;
    private final RequestServiceImpl requestServiceImpl;
    private final UserServiceImpl userServiceImpl;

    @Test
    void add() {
        UserDto userDto = new UserDto(null, "Jon", "jon@mail.ru");
        userServiceImpl.add(userDto);

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("тамагавк нужен")
                .items(new ArrayList<>())
                .build();
        requestServiceImpl.add(1L, itemRequestDto, LocalDateTime.now());

        TypedQuery<ItemRequest> query = em.createQuery("Select i from ItemRequest i where i.description = :description", ItemRequest.class);
        ItemRequest itemRequest = query.setParameter("description", itemRequestDto.getDescription()).getSingleResult();
        assertThat(itemRequest.getId(), notNullValue());
        assertThat(itemRequest.getDescription(), equalTo(itemRequestDto.getDescription()));

        assertThrows(ValidationException.class, () -> requestServiceImpl.listOfRequestsFromOtherUsers(1L, -1, 1));

    }

    @Test
    void getYourRequestsWithResponse() {
    }

    @Test
    void listOfRequestsFromOtherUsers() {
        UserDto userDto = new UserDto(null, "Jon", "jon@mail.ru");
        userServiceImpl.add(userDto);

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("тамагавк нужен")
                .items(new ArrayList<>())
                .build();
        requestServiceImpl.add(1L, itemRequestDto, LocalDateTime.now());

        TypedQuery<ItemRequest> query = em.createQuery("Select i from ItemRequest i where i.description = :description", ItemRequest.class);
        ItemRequest itemRequest = query.setParameter("description", itemRequestDto.getDescription()).getSingleResult();
        assertThat(itemRequest.getId(), notNullValue());
        assertThat(itemRequest.getDescription(), equalTo(itemRequestDto.getDescription()));

        assertThrows(ValidationException.class, () -> requestServiceImpl.listOfRequestsFromOtherUsers(1L, -1, 1));

    }

    @Test
    void getRequestsWithResponse() {
    }
}