package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequestMapperTest {
    LocalDateTime created = LocalDateTime.now();

    @Test
    void toItemRequestDto() {
        List<ItemDto> itemList = List.of();
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("тамагавк нужен")
                .items(itemList)
                .build();
        ItemRequest itemRequest = RequestMapper.toItemRequest(1L, itemRequestDto, created);
        assertEquals(itemRequest.getDescription(), itemRequestDto.getDescription());
        assertTrue(itemRequest.getCreated().equals(created));
    }

    @Test
    void toItemRequest() {
        ItemRequest itemRequest = ItemRequest.builder()
                .description("тамагавк нужен")
                .requestor(1L)
                .created(created)
                .build();
        ItemRequestDto itemRequestDto = RequestMapper.toItemRequestDto(itemRequest);
        assertEquals(itemRequestDto.getDescription(), itemRequest.getDescription());
        assertEquals(itemRequestDto.getRequestor(), itemRequest.getRequestor());
        assertTrue(itemRequestDto.getCreated().equals(created));
    }
}