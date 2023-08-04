package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.InMemoryItemStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service("ItemServiceImpl")
public class ItemServiceImpl implements ItemService {
    private final InMemoryItemStorage inMemoryItemStorage;
    private final UserServiceImpl userServiceImpl;

    @Autowired
    public ItemServiceImpl(@Qualifier("InMemoryItemStorage") ItemStorage itemStorage, UserServiceImpl userServiceImpl) {
        this.inMemoryItemStorage = (InMemoryItemStorage) itemStorage;
        this.userServiceImpl = userServiceImpl;
    }

    private void validationItem(ItemDto itemDto) {
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


    private void validationIdOwner(Long owner, UserServiceImpl userServiceImpl) {
        if ((inMemoryItemStorage.getListItems().size() != 0 && !inMemoryItemStorage.getListItems().containsKey(owner)) &&
                userServiceImpl.get(owner) == null) {
            log.info("Нет такого идентификатора владельца");
            throw new NotFoundException(String.format("Нет такого идентификатора владельца № %s", owner));
        }
    }

    private void validationIdItem(Long id) {
        for (List<Item> items : inMemoryItemStorage.getListItems().values()) {
            boolean findItem = items.stream().anyMatch(item -> id.equals(item.getId()));
            if (findItem) {
                return;
            }
        }
        log.info("Нет такого идентификатора");
        throw new NotFoundException(String.format("Нет такого идентификатора № %s", id));
    }


    @Override
    public ItemDto add(Long owner, ItemDto itemDto) {
        validationIdOwner(owner, userServiceImpl);    // проверка наличия id пользователя в памяти
        validationItem(itemDto);
        Item item = ItemMapper.toItem(owner, itemDto);
        return ItemMapper.toItemDto(inMemoryItemStorage.add(owner, item));
    }

    @Override
    public ItemDto get(Long id, Long owner) {
        validationIdOwner(owner, userServiceImpl);
        validationIdItem(id);
        return ItemMapper.toItemDto(inMemoryItemStorage.get(id));
    }

    @Override
    public ItemDto update(Long id, Long owner, ItemDto itemDto) {
        validationIdOwner(owner, userServiceImpl);
        validationIdItem(id);
        Item item = ItemMapper.toItem(owner, itemDto);
        return ItemMapper.toItemDto(inMemoryItemStorage.update(id, owner, item));
    }

    @Override
    public List<ItemDto> getAllItemtoUser(Long owner) {
        validationIdOwner(owner, userServiceImpl);
        return inMemoryItemStorage.getAllItemtoUser(owner).stream()
                .map(item -> ItemMapper.toItemDto(item))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAllItemWithText(String text, Long owner) {
        validationIdOwner(owner, userServiceImpl);
        if (text.isBlank() || text.isEmpty()) {
            return new ArrayList<>();
        }
        return inMemoryItemStorage.getAllItemWithText(text).stream()
                .map(item -> ItemMapper.toItemDto(item))
                .collect(Collectors.toList());
    }
}
