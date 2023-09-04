package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookingMapperTest {
    LocalDateTime start = LocalDateTime.now();
    LocalDateTime end = LocalDateTime.now().plusDays(1L);

    @Test
    void toBookingDto() {
        User user = new User("Jon", "jon@mail.ru");
        Booking booking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(new Item())
                .booker(user)
                .status(Status.WAITING)
                .build();
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);
        assertEquals(bookingDto.getBooker(), booking.getBooker());
        assertEquals(bookingDto.getStatus(), Status.WAITING);
        assertTrue(bookingDto.getStart().equals(start));
        assertTrue(bookingDto.getEnd().equals(end));

    }

    @Test
    void toBooking() {
        Item item = Item.builder()
                .id(1L)
                .name("молоток")
                .description("маленький")
                .available(true)
                .owner(1L)
                .requestId(null)
                .build();
        User user = new User("Jon", "jon@mail.ru");
        RequestBookingDto requestBookingDto = new RequestBookingDto(start, end, 1L);
        Booking booking = BookingMapper.toBooking(item, user, requestBookingDto, Status.WAITING);
        assertTrue(booking.getItem().getId().equals(1L));
        assertTrue(booking.getStart().equals(start));
        assertTrue(booking.getEnd().equals(end));
    }
}