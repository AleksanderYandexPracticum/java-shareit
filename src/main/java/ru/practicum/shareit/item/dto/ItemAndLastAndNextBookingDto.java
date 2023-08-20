package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingNewNameIdDto;

import java.util.List;


@Data
public class ItemAndLastAndNextBookingDto {

    private Long id;
    private String name;
    private String description; //развёрнутое описание
    private Boolean available;  //статус о том, доступна или нет вещь для аренды
    private Long request;
    private BookingNewNameIdDto lastBooking;
    private BookingNewNameIdDto nextBooking;
    private List<CommentCreatedStringDto> comments;

    public ItemAndLastAndNextBookingDto(Long id, String name, String description, Boolean available,
                                        BookingNewNameIdDto lastBooking, BookingNewNameIdDto nextBooking,
                                        List<CommentCreatedStringDto> comments) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
        this.comments = comments;
    }
}
