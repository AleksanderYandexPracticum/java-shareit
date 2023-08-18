package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingNewNameIdDto;

@Data
public class ItemAndLastAndNextBookingDto {

    private Long id;
    private String name;
    private String description; //развёрнутое описание
    private Boolean available;  //статус о том, доступна или нет вещь для аренды
    private Long request;
    private BookingNewNameIdDto lastBooking;
    private BookingNewNameIdDto nextBooking;

    public ItemAndLastAndNextBookingDto(Long id, String name, String description, Boolean available,
                                        BookingNewNameIdDto lastBooking, BookingNewNameIdDto nextBooking) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;

    }
}
