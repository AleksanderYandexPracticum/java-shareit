package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.dto.BookingNewNameIdDto;
import ru.practicum.shareit.item.dto.CommentCreatedStringDto;
import ru.practicum.shareit.item.dto.ItemAndLastAndNextBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest() != null ? item.getRequest() : null)
                .build();
    }

    public static Item toItem(Long owner, ItemDto itemDto) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .request(itemDto.getRequest() != null ? itemDto.getRequest() : null)
                .build();
    }


    public static ItemAndLastAndNextBookingDto toItemAndLastAndNextBookingDto(Item item,
                                                                              BookingNewNameIdDto lastBooking,
                                                                              BookingNewNameIdDto nextBooking,
                                                                              List<CommentCreatedStringDto> commentsCreatedStringDto) {
        return ItemAndLastAndNextBookingDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(commentsCreatedStringDto)
                .build();
    }
}