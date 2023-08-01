package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemServiceImpl itemServiceImpl;
    private final UserServiceImpl userServiceImpl;

    @Autowired
    public ItemController(@Qualifier("ItemServiceImpl") ItemService itemService, UserServiceImpl userServiceImpl) {
        this.itemServiceImpl = (ItemServiceImpl) itemService;
        this.userServiceImpl = userServiceImpl;
    }


    @PostMapping
    public ItemDto add(HttpServletRequest request, @RequestHeader("X-Sharer-User-Id") Long owner, @RequestBody ItemDto itemDto) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        itemServiceImpl.validationIdOwner(owner, userServiceImpl);    // проверка наличия id пользователя в памяти
        itemServiceImpl.validationItem(itemDto);
        return itemServiceImpl.add(owner, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(HttpServletRequest request, @RequestHeader("X-Sharer-User-Id") Long owner,
                          @PathVariable("itemId") Long id, @RequestBody ItemDto itemDto) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        itemServiceImpl.validationIdOwner(owner, userServiceImpl);
        itemServiceImpl.validationIdItem(id);
        return itemServiceImpl.update(id, owner, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getUser(HttpServletRequest request, @RequestHeader("X-Sharer-User-Id") Long owner, @PathVariable("itemId") Long id) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        itemServiceImpl.validationIdOwner(owner, userServiceImpl);
        itemServiceImpl.validationIdItem(id);
        return itemServiceImpl.get(id);
    }

    @GetMapping
    public List<ItemDto> getAllItemtoUser(HttpServletRequest request, @RequestHeader("X-Sharer-User-Id") Long owner) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        itemServiceImpl.validationIdOwner(owner, userServiceImpl);
        return itemServiceImpl.getAllItemtoUser(owner);
    }

    @GetMapping("/search")
    public List<ItemDto> getAllItemWithText(HttpServletRequest request, @RequestHeader("X-Sharer-User-Id") Long owner,
                                            @RequestParam(value = "text") String text) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        itemServiceImpl.validationIdOwner(owner, userServiceImpl);
        return itemServiceImpl.getAllItemWithText(text);
    }

}
