package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BookingNewNameIdDto {
    private Long id;
    private Long bookerId;

    public BookingNewNameIdDto(Long id, Long bookerId) {
        this.id = id;
        this.bookerId = bookerId;
    }
}
