package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component("InMemoryItemStorage")
public class InMemoryItemStorage implements ItemStorage {
    private HashMap<Long, List<Item>> listItems = new HashMap<>();
    private static Long id = 1L;

    public HashMap<Long, List<Item>> getListItems() {
        return listItems;
    }

    @Override
    public ItemDto add(Long owner, ItemDto itemDto) {
        Item item = ItemMapper.toItem(id, owner, itemDto);
        if (!listItems.containsKey(owner)) {
            List<Item> items = new ArrayList<>();
            items.add(item);
            listItems.put(owner, items);
        } else if (listItems.containsKey(owner) && listItems.get(owner) == null) {
            List<Item> items = new ArrayList<>();
            items.add(item);
            listItems.put(owner, items);
        } else {
            listItems.get(owner).add(item);
        }
        itemDto.setId(id);
        id++;
        return itemDto;
    }

    @Override
    public ItemDto get(Long id) {
        for (List<Item> items : listItems.values()) {
            Item findItem = items.stream().filter(item -> id.equals(item.getId())).findFirst().orElse(null);
            if (findItem != null) {
                return ItemMapper.toItemDto(findItem);
            }
        }
        throw new NotFoundException(String.format("Нет такого идентификатора № %s", id));
    }

    @Override
    public ItemDto update(Long id, Long owner, ItemDto itemDto) {
        if (!listItems.containsKey(owner)) {
            throw new NotFoundException(String.format("У Владельца  № %s нет вещи с идентификатором  № %s", owner, id));
        }
        List<Item> items = listItems.get(owner);
        Item findItem = items.stream().filter(item -> id.equals(item.getId())).findFirst().orElse(null);
        if (findItem != null) {
            items.remove(findItem);
            findItem.setName(itemDto.getName() == null ? findItem.getName() : itemDto.getName());
            findItem.setDescription(itemDto.getDescription() == null ? findItem.getDescription() : itemDto.getDescription());
            findItem.setAvailable(itemDto.getAvailable() == null ? findItem.getAvailable() : itemDto.getAvailable());
            findItem.setRequest(itemDto.getRequest() == null ? findItem.getRequest() : itemDto.getRequest());

            items.add(findItem);
        } else {
            throw new NotFoundException(String.format("Нет такого идентификатора № %s", id));
        }
        return ItemMapper.toItemDto(findItem);
    }

    @Override
    public List<ItemDto> getAllItemtoUser(Long owner) {
        List<Item> itemList = listItems.get(owner);
        return itemList.stream().map(item -> ItemMapper.toItemDto(item)).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAllItemWithText(String text) {
        List<ItemDto> findItems = new ArrayList<>();
        Pattern p = Pattern.compile(text.toUpperCase());

        for (List<Item> items : listItems.values()) {
            for (Item item : items) {
                String name = item.getName();
                String description = item.getDescription();

                Matcher matcherName = p.matcher(name.toUpperCase());
                Matcher matcherDescription = p.matcher(description.toUpperCase());

                if (item.getAvailable() && (matcherName.find() || matcherDescription.find())) {
                    findItems.add(ItemMapper.toItemDto(item));
                }
            }
        }
        return findItems;
    }
}