package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto add(Long owner, ItemDto itemDto);

    ItemDto get(Long id, Long owner);

    ItemDto update(Long id, Long owner, ItemDto itemDto);

    List<ItemDto> getAllItemtoUser(Long owner);

    List<ItemDto> getAllItemWithText(String text, Long owner);
}
