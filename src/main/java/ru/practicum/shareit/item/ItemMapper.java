package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest() : null);
    }

    public static Item toItem(Long id, Long owner, ItemDto itemDto) {
        return new Item(id,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                owner,
                itemDto.getRequest() != null ? itemDto.getRequest() : null);
    }
}
