package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.persistence.EntityManager;

import java.time.LocalDateTime;
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
class BookingServiceImplIntegrationTest {

    private final EntityManager em;
    private final BookingServiceImpl bookingServiceImpl;
    private final UserServiceImpl userServiceImpl;
    private final ItemServiceImpl itemServiceImpl;


    @Test
    void add() {
        UserDto userDto = new UserDto(null, "Jon", "jon@mail.ru");
        userServiceImpl.add(userDto);

        UserDto userDto1 = new UserDto(null, "PegonJon", "pegonjon@mail.ru");
        userServiceImpl.add(userDto1);

        ItemDto itemDto = ItemDto.builder()
                .name("молоток")
                .description("маленький")
                .available(true)
                .build();
        itemServiceImpl.add(1L, itemDto);

        ItemDto itemDto1 = ItemDto.builder()
                .name("молоток")
                .description("маленький")
                .available(false)
                .build();
        itemServiceImpl.add(1L, itemDto1);


        LocalDateTime start = LocalDateTime.now().plusDays(1L);
        LocalDateTime end = LocalDateTime.now().plusDays(2L);
        RequestBookingDto requestBookingDto = new RequestBookingDto(start, end, 1L);

        BookingDto actualBookingDto = bookingServiceImpl.add(2L, requestBookingDto, LocalDateTime.now());

        assertThat(actualBookingDto.getId(), notNullValue());
        assertThat(actualBookingDto.getStart(), equalTo(requestBookingDto.getStart()));
        assertThat(actualBookingDto.getEnd(), equalTo(requestBookingDto.getEnd()));
        assertThat(actualBookingDto.getItem().getId(), equalTo(1L));

        RequestBookingDto requestBookingDto1 = new RequestBookingDto(start, end, 2L);
        assertThrows(ValidationException.class, () -> bookingServiceImpl.add(2L, requestBookingDto1, LocalDateTime.now()));

        RequestBookingDto requestBookingDto2 = new RequestBookingDto(start, end, 3L);
        assertThrows(NotFoundException.class, () -> bookingServiceImpl.add(2L, requestBookingDto2, LocalDateTime.now()));

        assertThrows(NotFoundException.class, () -> bookingServiceImpl.add(3L, requestBookingDto, LocalDateTime.now()));

        assertThrows(NotFoundException.class, () -> bookingServiceImpl.add(1L, requestBookingDto, LocalDateTime.now()));
    }

    @Test
    void update() {
        UserDto userDto = new UserDto(null, "Jon", "jon@mail.ru");
        UserDto returnUserDto = userServiceImpl.add(userDto);

        UserDto userDto1 = new UserDto(null, "PegonJon", "pegonjon@mail.ru");
        UserDto returnUserDto1 = userServiceImpl.add(userDto1);

        ItemDto itemDto = ItemDto.builder()
                .name("молоток")
                .description("маленький")
                .available(true)
                .build();
        itemServiceImpl.add(1L, itemDto);

        ItemDto itemDto1 = ItemDto.builder()
                .name("Кувалда")
                .description("Большая")
                .available(false)
                .build();
        itemServiceImpl.add(1L, itemDto1);


        LocalDateTime start = LocalDateTime.now().plusDays(1L);
        LocalDateTime end = LocalDateTime.now().plusDays(2L);

        RequestBookingDto requestBookingDto = new RequestBookingDto(start, end, 1L);

        BookingDto newBookingDto = bookingServiceImpl.add(2L, requestBookingDto, LocalDateTime.now());

        BookingDto bookingDto = bookingServiceImpl.update(1L, 1L, true);

        assertThat(bookingDto.getStart(), equalTo(requestBookingDto.getStart()));
        assertThat(bookingDto.getEnd(), equalTo(requestBookingDto.getEnd()));
        assertThat(bookingDto.getItem().getId(), equalTo(1L));
        assertThat(bookingDto.getStatus(), equalTo(Status.APPROVED));

        assertThrows(NotFoundException.class, () -> bookingServiceImpl.update(2L, 1L, false));
        assertThrows(ValidationException.class, () -> bookingServiceImpl.update(1L, 1L, false));

    }

    @Test
    void getById() {
        UserDto userDto = new UserDto(null, "Jon", "jon@mail.ru");
        UserDto returnUserDto = userServiceImpl.add(userDto);

        UserDto userDto1 = new UserDto(null, "PegonJon", "pegonjon@mail.ru");
        UserDto returnUserDto1 = userServiceImpl.add(userDto1);

        ItemDto itemDto = ItemDto.builder()
                .name("молоток")
                .description("маленький")
                .available(true)
                .build();
        itemServiceImpl.add(1L, itemDto);

        ItemDto itemDto1 = ItemDto.builder()
                .name("Кувалда")
                .description("Большая")
                .available(false)
                .build();
        itemServiceImpl.add(1L, itemDto1);


        LocalDateTime start = LocalDateTime.now().plusDays(1L);
        LocalDateTime end = LocalDateTime.now().plusDays(2L);

        RequestBookingDto requestBookingDto = new RequestBookingDto(start, end, 1L);

        bookingServiceImpl.add(2L, requestBookingDto, LocalDateTime.now());

        BookingDto bookingDto = bookingServiceImpl.getById(1L, 2L);

        assertThat(bookingDto.getStart(), equalTo(requestBookingDto.getStart()));
        assertThat(bookingDto.getEnd(), equalTo(requestBookingDto.getEnd()));
        assertThat(bookingDto.getItem().getId(), equalTo(1L));

        assertThrows(NotFoundException.class, () -> bookingServiceImpl.getById(3L, 2L));
        assertThrows(NotFoundException.class, () -> bookingServiceImpl.getById(1L, 3L));
    }

    @Test
    void getAllBookingsByUserId() {
        UserDto userDto = new UserDto(null, "Jon", "jon@mail.ru");
        UserDto returnUserDto = userServiceImpl.add(userDto);

        UserDto userDto1 = new UserDto(null, "PegonJon", "pegonjon@mail.ru");
        UserDto returnUserDto1 = userServiceImpl.add(userDto1);

        ItemDto itemDto = ItemDto.builder()
                .name("молоток")
                .description("маленький")
                .available(true)
                .build();
        itemServiceImpl.add(1L, itemDto);

        ItemDto itemDto1 = ItemDto.builder()
                .name("Кувалда")
                .description("Большая")
                .available(false)
                .build();
        itemServiceImpl.add(1L, itemDto1);


        LocalDateTime start = LocalDateTime.now().plusDays(1L);
        LocalDateTime end = LocalDateTime.now().plusDays(2L);

        RequestBookingDto requestBookingDto = new RequestBookingDto(start, end, 1L);

        bookingServiceImpl.add(2L, requestBookingDto, LocalDateTime.now());

        List<BookingDto> list = bookingServiceImpl.getAllBookingsByUserId(
                2L, "ALL", 0, 1);

        assertThat(list.get(0).getStart(), equalTo(requestBookingDto.getStart()));
        assertThat(list.get(0).getEnd(), equalTo(requestBookingDto.getEnd()));
        assertThat(list.get(0).getItem().getId(), equalTo(1L));

        assertThrows(NotFoundException.class, () -> bookingServiceImpl.getAllBookingsByUserId(
                3L, "ALL", 0, 1));


        list = bookingServiceImpl.getAllBookingsByUserId(
                2L, "ALL", 0, 1);
        assertTrue(list.size() == 1);
        list = bookingServiceImpl.getAllBookingsByUserId(
                2L, "WAITING", 0, 1);
        assertTrue(list.size() == 1);
        list = bookingServiceImpl.getAllBookingsByUserId(
                2L, "REJECTED", 0, 1);
        assertTrue(list.size() == 0);
        list = bookingServiceImpl.getAllBookingsByUserId(
                2L, "CURRENT", 0, 1);
        assertTrue(list.size() == 0);
        list = bookingServiceImpl.getAllBookingsByUserId(
                2L, "PAST", 0, 1);
        assertTrue(list.size() == 0);
        list = bookingServiceImpl.getAllBookingsByUserId(
                2L, "FUTURE", 0, 1);
        assertTrue(list.size() == 1);

        list = bookingServiceImpl.getAllBookingsByUserId(
                2L, "ALL", null, null);
        assertTrue(list.size() == 1);
        list = bookingServiceImpl.getAllBookingsByUserId(
                2L, "WAITING", null, null);
        assertTrue(list.size() == 1);
        list = bookingServiceImpl.getAllBookingsByUserId(
                2L, "REJECTED", null, null);
        assertTrue(list.size() == 0);
        list = bookingServiceImpl.getAllBookingsByUserId(
                2L, "CURRENT", null, null);
        assertTrue(list.size() == 0);
        list = bookingServiceImpl.getAllBookingsByUserId(
                2L, "PAST", null, null);
        assertTrue(list.size() == 0);
        list = bookingServiceImpl.getAllBookingsByUserId(
                2L, "FUTURE", null, null);
        assertTrue(list.size() == 1);

    }

    @Test
    void getAllBookingsAllItemsByUserId() {

        UserDto userDto = new UserDto(null, "Jon", "jon@mail.ru");
        userServiceImpl.add(userDto);

        UserDto userDto1 = new UserDto(null, "PegonJon", "pegonjon@mail.ru");
        userServiceImpl.add(userDto1);

        ItemDto itemDto = ItemDto.builder()
                .name("молоток")
                .description("маленький")
                .available(true)
                .build();
        itemServiceImpl.add(1L, itemDto);

        ItemDto itemDto1 = ItemDto.builder()
                .name("Кувалда")
                .description("Большая")
                .available(false)
                .build();
        itemServiceImpl.add(1L, itemDto1);


        LocalDateTime start = LocalDateTime.now().plusDays(1L);
        LocalDateTime end = LocalDateTime.now().plusDays(2L);

        RequestBookingDto requestBookingDto = new RequestBookingDto(start, end, 1L);

        bookingServiceImpl.add(2L, requestBookingDto, LocalDateTime.now());

        List<BookingDto> list = bookingServiceImpl.getAllBookingsAllItemsByUserId(
                1L, "ALL", 0, 1);

        assertThat(list.get(0).getStart(), equalTo(requestBookingDto.getStart()));
        assertThat(list.get(0).getEnd(), equalTo(requestBookingDto.getEnd()));
        assertThat(list.get(0).getItem().getId(), equalTo(1L));

        assertThrows(NotFoundException.class, () -> bookingServiceImpl.getAllBookingsAllItemsByUserId(
                3L, "ALL", 0, 1));


        list = bookingServiceImpl.getAllBookingsAllItemsByUserId(
                1L, "ALL", 0, 1);
        assertTrue(list.size() == 1);
        list = bookingServiceImpl.getAllBookingsAllItemsByUserId(
                1L, "WAITING", 0, 1);
        assertTrue(list.size() == 1);
        list = bookingServiceImpl.getAllBookingsAllItemsByUserId(
                1L, "REJECTED", 0, 1);
        assertTrue(list.size() == 0);
        list = bookingServiceImpl.getAllBookingsAllItemsByUserId(
                1L, "CURRENT", 0, 1);
        assertTrue(list.size() == 0);
        list = bookingServiceImpl.getAllBookingsAllItemsByUserId(
                1L, "PAST", 0, 1);
        assertTrue(list.size() == 0);
        list = bookingServiceImpl.getAllBookingsAllItemsByUserId(
                1L, "FUTURE", 0, 1);
        assertTrue(list.size() == 1);

        list = bookingServiceImpl.getAllBookingsAllItemsByUserId(
                1L, "ALL", null, null);
        assertTrue(list.size() == 1);
        list = bookingServiceImpl.getAllBookingsAllItemsByUserId(
                1L, "WAITING", null, null);
        assertTrue(list.size() == 1);
        list = bookingServiceImpl.getAllBookingsAllItemsByUserId(
                1L, "REJECTED", null, null);
        assertTrue(list.size() == 0);
        list = bookingServiceImpl.getAllBookingsAllItemsByUserId(
                1L, "CURRENT", null, null);
        assertTrue(list.size() == 0);
        list = bookingServiceImpl.getAllBookingsAllItemsByUserId(
                1L, "PAST", null, null);
        assertTrue(list.size() == 0);
        list = bookingServiceImpl.getAllBookingsAllItemsByUserId(
                1L, "FUTURE", null, null);
        assertTrue(list.size() == 1);

    }
}