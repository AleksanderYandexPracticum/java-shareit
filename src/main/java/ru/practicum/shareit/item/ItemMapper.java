package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.dto.BookingNewNameIdDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest() : null);
    }

    public static Item toItem(Long owner, ItemDto itemDto) {
        return new Item(itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                owner,
                itemDto.getRequest() != null ? itemDto.getRequest() : null);
    }


    public static ItemAndLastAndNextBookingDto toItemAndLastAndNextBookingDto(Item item,
                                                                              BookingNewNameIdDto lastBooking,
                                                                              BookingNewNameIdDto nextBooking,
                                                                              List<CommentCreatedStringDto> commentsCreatedStringDto) {
        return new ItemAndLastAndNextBookingDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBooking,
                nextBooking,
                commentsCreatedStringDto);
    }
}