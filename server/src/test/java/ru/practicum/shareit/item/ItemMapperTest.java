package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemAndLastAndNextBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemMapperTest {

    @Test
    void toItemDto() {
        Item item = Item.builder()
                .id(1L)
                .name("молоток")
                .description("маленький")
                .available(true)
                .owner(1L)
                .requestId(null)
                .build();
        ItemDto itemDto = ItemMapper.toItemDto(item);
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getAvailable(), item.getAvailable());
    }

    @Test
    void toItem() {
        ItemDto itemDto = ItemDto.builder()
                .name("молоток_NEW")
                .description("маленький_NEW")
                .available(true)
                .requestId(null)
                .build();
        Item item = ItemMapper.toItem(1L, itemDto);
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
    }

    @Test
    void toItemAndLastAndNextBookingDto() {

        Item item = Item.builder()
                .id(1L)
                .name("молоток")
                .description("маленький")
                .available(true)
                .owner(1L)
                .requestId(null)
                .build();

        ItemAndLastAndNextBookingDto itemAndLastAndNextBookingDto = ItemMapper.toItemAndLastAndNextBookingDto(item,
                null,
                null,
                null);
        assertEquals(itemAndLastAndNextBookingDto.getName(), item.getName());
        assertEquals(itemAndLastAndNextBookingDto.getDescription(), item.getDescription());
        assertEquals(itemAndLastAndNextBookingDto.getAvailable(), item.getAvailable());
        assertEquals(itemAndLastAndNextBookingDto.getLastBooking(), null);
        assertEquals(itemAndLastAndNextBookingDto.getNextBooking(), null);
        assertEquals(itemAndLastAndNextBookingDto.getComments(), null);
    }
}