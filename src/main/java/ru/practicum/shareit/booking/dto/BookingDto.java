package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
public class BookingDto {

    private Long id;
    private LocalDateTime start;   //дата и время начала бронирования
    private LocalDateTime end;  //дата и время конца бронирования
    private Item item;  //вещь, которую пользователь бронирует
    private User booker;  //пользователь, который осуществляет бронирование
    private Status status;

    public BookingDto(Long id, LocalDateTime start, LocalDateTime end, Item item, User booker, Status status) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.item = item;
        this.booker = booker;
        this.status = status;
    }
}