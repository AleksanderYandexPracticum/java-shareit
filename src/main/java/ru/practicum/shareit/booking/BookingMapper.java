package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingWithoutTimeDTO;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus());
    }

    public static BookingWithoutTimeDTO toBookingWithoutTimeDTO(Booking booking) {
        return new BookingWithoutTimeDTO(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus());
    }

    public static Booking toBooking(Item item, User user, RequestBookingDto requestBookingDto, Status status) {
        return new Booking(requestBookingDto.getStart(),
                requestBookingDto.getEnd(),
                item,
                user,
                status);
    }

}
