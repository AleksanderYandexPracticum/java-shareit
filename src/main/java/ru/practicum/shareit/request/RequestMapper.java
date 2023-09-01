package ru.practicum.shareit.request;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class RequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestor(itemRequest.getRequestor())
                .created(itemRequest.getCreated())
                .items(new ArrayList<ItemDto>())
                .build();
    }

    public static ItemRequest toItemRequest(Long owner, ItemRequestDto itemRequestDto, LocalDateTime createdTime) {
        return ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .requestor(owner)
                .created(createdTime)
                .build();
    }
}
