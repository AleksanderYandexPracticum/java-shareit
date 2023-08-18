package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.dto.BookingNewNameIdDto;
import ru.practicum.shareit.item.dto.ItemAndLastAndNextBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithTimeDto;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

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

    public static ItemWithTimeDto toitemWithTimeDto(Item item, LocalDateTime nearStart, LocalDateTime nearEnd) {
        return new ItemWithTimeDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                item.getRequest(),
                nearStart,
                nearEnd);
    }

    public static ItemAndLastAndNextBookingDto toItemAndLastAndNextBookingDto(Item item,
                                                                              BookingNewNameIdDto lastBooking,
                                                                              BookingNewNameIdDto nextBooking) {
        return new ItemAndLastAndNextBookingDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBooking,
                nextBooking);
    }

}