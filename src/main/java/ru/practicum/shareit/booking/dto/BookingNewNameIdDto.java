package ru.practicum.shareit.booking.dto;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class BookingNewNameIdDto {
    private Long id;
    private Long bookerId;

}
