package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class RequestBookingDto {
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;

}
