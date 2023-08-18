package ru.practicum.shareit.booking.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestBookingDto {
    private LocalDateTime start;   //дата и время начала бронирования
    private LocalDateTime end;  //дата и время конца бронирования
    private Long itemId;  //вещь, которую пользователь бронирует

    public RequestBookingDto(LocalDateTime start, LocalDateTime end, Long itemId) {
        this.start = start;
        this.end = end;
        this.itemId = itemId;
    }
}
