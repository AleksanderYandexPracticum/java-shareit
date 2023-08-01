package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.InMemoryItemStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service("ItemServiceImpl")
public class ItemServiceImpl implements ItemService {
    private final InMemoryItemStorage inMemoryItemStorage;

    @Autowired
    public ItemServiceImpl(@Qualifier("InMemoryItemStorage") ItemStorage itemStorage) {
        this.inMemoryItemStorage = (InMemoryItemStorage) itemStorage;
    }

    public void validationItem(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            log.info("Название вещи не может быть пустым");
            throw new ValidationException("Название вещи не может быть пустым");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            log.info("Описание не может быть пустым");
            throw new ValidationException("Описание не может быть пустым");
        }
        if (itemDto.getAvailable() == null) {
            log.info("Статус аренды не может быть пустым");
            throw new ValidationException("Статус аренды не может быть пустым");
        }
    }


    public void validationIdOwner(Long owner, UserServiceImpl userServiceImpl) {
        if ((inMemoryItemStorage.getListItems().size() != 0 && !inMemoryItemStorage.getListItems().containsKey(owner)) &&
                !userServiceImpl.getInMemoryUserStorage().getListUsers().containsKey(owner)) {
            log.info("Нет такого идентификатора владельца");
            throw new NotFoundException(String.format("Нет такого идентификатора владельца № %s", owner));
        }
    }

    public void validationIdItem(Long id) {
        Item findItem = null;
        for (List<Item> items : inMemoryItemStorage.getListItems().values()) {
            findItem = items.stream().filter(item -> id.equals(item.getId())).findFirst().orElse(null);
            if (findItem != null) {
                return;
            }
        }
            log.info("Нет такого идентификатора");
            throw new NotFoundException(String.format("Нет такого идентификатора № %s", id));
    }


    @Override
    public ItemDto add(Long owner, ItemDto itemDto) {
        return inMemoryItemStorage.add(owner, itemDto);
    }

    @Override
    public ItemDto get(Long id) {
        return inMemoryItemStorage.get(id);
    }

    @Override
    public ItemDto update(Long id, Long owner, ItemDto itemDto) {
        return inMemoryItemStorage.update(id, owner, itemDto);
    }

    @Override
    public List<ItemDto> getAllItemtoUser(Long owner) {
        return inMemoryItemStorage.getAllItemtoUser(owner);
    }

    @Override
    public List<ItemDto> getAllItemWithText(String text) {
        if (text.isBlank() || text.isEmpty()) {
            return new ArrayList<>();
        }
        return inMemoryItemStorage.getAllItemWithText(text);
    }
}
