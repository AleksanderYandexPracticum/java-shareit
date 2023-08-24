package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {

    BookingDto add(Long id, RequestBookingDto requestBookingDto, LocalDateTime time);

    BookingDto update(Long owner, Long id, Boolean approved);

    BookingDto getById(Long id, Long ownerOrBooker);

    List<BookingDto> getAllBookingsByUserId(Long id, String state);

    List<BookingDto> getAllBookingsAllItemsByUserId(Long id, String state);
}