package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();
    }

    public static Booking toBooking(Item item, User user, RequestBookingDto requestBookingDto, Status status) {
        return Booking.builder()
                .start(requestBookingDto.getStart())
                .end(requestBookingDto.getEnd())
                .item(item)
                .booker(user)
                .status(status)
                .build();
    }

}
