package ru.practicum.shareit.request.Service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
class RequestServiceImplIntegrationTest {

    private final EntityManager em;
    private final RequestServiceImpl requestServiceImpl;
    private final UserServiceImpl userServiceImpl;
    private final ItemServiceImpl itemServiceImpl;

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

//        assertThrows(ValidationException.class, () -> requestServiceImpl.listOfRequestsFromOtherUsers(1L, -1, 1));

        assertThrows(NotFoundException.class, () -> requestServiceImpl.add(2L, itemRequestDto, LocalDateTime.now()));
//        ItemRequestDto itemRequestDto1 = ItemRequestDto.builder()
//                .description("")
//                .items(new ArrayList<>())
//                .build();
//        assertThrows(ValidationException.class, () -> requestServiceImpl.add(1L, itemRequestDto1, LocalDateTime.now()));


    }

    @Test
    void getYourRequestsWithResponse() {
        UserDto userDto = new UserDto(null, "Jon", "jon@mail.ru");
        userServiceImpl.add(userDto);

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("тамагавк нужен")
                .items(new ArrayList<>())
                .build();
        requestServiceImpl.add(1L, itemRequestDto, LocalDateTime.now());

        assertThrows(NotFoundException.class, () -> requestServiceImpl.getYourRequestsWithResponse(2L));

        ItemDto itemDto = ItemDto.builder()
                .name("тамагавк")
                .description("огромный")
                .available(true)
                .requestId(1L)
                .build();
        itemServiceImpl.add(1L, itemDto);
        List<ItemRequestDto> list = requestServiceImpl.getYourRequestsWithResponse(1L);
        assertTrue(list.size() == 1);
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

//        assertThrows(ValidationException.class, () -> requestServiceImpl.listOfRequestsFromOtherUsers(1L, -1, 1));

        List<ItemRequestDto> list = requestServiceImpl.listOfRequestsFromOtherUsers(2L, 0, 1);
        assertThat(list.get(0).getDescription(), equalTo(itemRequestDto.getDescription()));
    }

    @Test
    void getRequestsWithResponse() {

        UserDto userDto = new UserDto(null, "Jon", "jon@mail.ru");
        userServiceImpl.add(userDto);

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("тамагавк нужен")
                .items(new ArrayList<>())
                .build();
        requestServiceImpl.add(1L, itemRequestDto, LocalDateTime.now());

        ItemRequestDto returnItemRequestDto = requestServiceImpl.getRequestsWithResponse(1L, 1L);
        assertThat(returnItemRequestDto.getDescription(), equalTo(itemRequestDto.getDescription()));

        assertThrows(NotFoundException.class, () -> requestServiceImpl.getRequestsWithResponse(2L, 1L));
        assertThrows(NotFoundException.class, () -> requestServiceImpl.getRequestsWithResponse(1L, 2L));
    }
}