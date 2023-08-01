package ru.practicum.shareit.booking.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class Booking {
    private Long id;
    private LocalDateTime start;   //дата и время начала бронирования
    private LocalDateTime end;  //дата и время конца бронирования
    private Long item;  //вещь, которую пользователь бронирует
    private Long booker;  //пользователь, который осуществляет бронирование
    private String status;  //статус бронирования
    // WAITING — новое бронирование, ожидает одобрения
    // APPROVED — бронирование подтверждено владельцем
    // REJECTED — бронирование отклонено владельцем
    // CANCELED — бронирование отменено создателем

    public Booking(Long id, LocalDateTime start, LocalDateTime end, Long item, Long booker, String status) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.item = item;
        this.booker = booker;
        this.status = status;
    }
}
