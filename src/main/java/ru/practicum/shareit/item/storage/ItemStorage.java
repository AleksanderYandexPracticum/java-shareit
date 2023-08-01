package ru.practicum.shareit.item.storage;


import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemStorage {

    ItemDto add(Long owner, ItemDto itemDto);

    ItemDto get(Long id);

    ItemDto update(Long id, Long owner, ItemDto itemDto);

    List<ItemDto> getAllItemtoUser(Long owner);

    List<ItemDto> getAllItemWithText(String text);

}
