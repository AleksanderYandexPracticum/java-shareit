package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemAndLastAndNextBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto add(Long owner, ItemDto itemDto);

    ItemAndLastAndNextBookingDto get(Long id, Long owner);

    ItemDto update(Long id, Long owner, ItemDto itemDto);

    List<ItemAndLastAndNextBookingDto> getAllItemToUser(Long owner, Integer from, Integer size);

    List<ItemDto> getAllItemWithText(String text, Long owner, Integer from, Integer size);

    CommentDto addComment(Long owner, Long id, CommentDto commentDto);
}