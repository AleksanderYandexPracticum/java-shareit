package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingNewNameIdDto;

import java.util.List;

@Getter
@Setter
@Builder
public class ItemAndLastAndNextBookingDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private BookingNewNameIdDto lastBooking;
    private BookingNewNameIdDto nextBooking;
    private List<CommentCreatedStringDto> comments;
}
