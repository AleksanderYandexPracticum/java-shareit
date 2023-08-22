package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class RequestBookingDto {
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;

}
