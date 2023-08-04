package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component("InMemoryItemStorage")
public class InMemoryItemStorage implements ItemStorage {
    private Map<Long, List<Item>> listItems = new HashMap<>();
    private static Long id = 1L;  // id вещи

    public Map<Long, List<Item>> getListItems() {
        return listItems;
    }

    @Override
    public Item add(Long owner, Item item) {
        item.setId(id);
        if (!listItems.containsKey(owner) || (listItems.containsKey(owner) && listItems.get(owner) == null)) {
            List<Item> items = new ArrayList<>();
            items.add(item);
            listItems.put(owner, items);
            log.info("Добавлена вещь № " + item.getId() + "  name =" + item.getName() + "  Владелец № " + owner);
        } else {
            listItems.get(owner).add(item);
            log.info("Добавлена вещь № " + item.getId() + "  name =" + item.getName() + "  Владелец № " + owner);
        }
        id++;
        return item;
    }

    @Override
    public Item get(Long id) {
        for (List<Item> items : listItems.values()) {
            Item findItem = items.stream()
                    .filter(item -> id.equals(item.getId()))
                    .findFirst()
                    .orElse(null);
            if (findItem != null) {
                return findItem;
            }
        }
        throw new NotFoundException(String.format("Нет такого идентификатора № %s", id));
    }

    @Override
    public Item update(Long id, Long owner, Item item) {
        item.setId(id);
        if (!listItems.containsKey(owner)) {
            throw new NotFoundException(String.format("У Владельца  № %s нет вещи с идентификатором  № %s", owner, id));
        }
        List<Item> items = listItems.get(owner);
        Item findItem = items.stream()
                .filter(i -> id.equals(i.getId()))
                .findFirst()
                .orElse(null);
        if (findItem != null) {
            items.remove(findItem);
            findItem.setName(item.getName() == null ? findItem.getName() : item.getName());
            findItem.setDescription(item.getDescription() == null ? findItem.getDescription() : item.getDescription());
            findItem.setAvailable(item.getAvailable() == null ? findItem.getAvailable() : item.getAvailable());
            findItem.setRequest(item.getRequest() == null ? findItem.getRequest() : item.getRequest());

            items.add(findItem);
        } else {
            throw new NotFoundException(String.format("Нет такого идентификатора № %s", id));
        }
        return findItem;
    }

    @Override
    public List<Item> getAllItemtoUser(Long owner) {
        return listItems.get(owner);
    }

    @Override
    public List<Item> getAllItemWithText(String text) {
        List<Item> findItems = new ArrayList<>();
        Pattern p = Pattern.compile(text.toUpperCase());

        for (List<Item> items : listItems.values()) {
            for (Item item : items) {
                String name = item.getName();
                String description = item.getDescription();

                Matcher matcherName = p.matcher(name.toUpperCase());
                Matcher matcherDescription = p.matcher(description.toUpperCase());

                if (item.getAvailable() && (matcherName.find() || matcherDescription.find())) {
                    findItems.add(item);
                }
            }
        }
        return findItems;
    }
}