package ru.practicum.shareit.item.storage;


import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item add(Long owner, Item item);

    Item get(Long id);

    Item update(Long id, Long owner, Item item);

    List<Item> getAllItemtoUser(Long owner);

    List<Item> getAllItemWithText(String text);

}
