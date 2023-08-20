package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemAndLastAndNextBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemServiceImpl itemServiceImpl;

    @Autowired
    public ItemController(@Qualifier("ItemServiceImpl") ItemService itemService) {
        this.itemServiceImpl = (ItemServiceImpl) itemService;
    }


    @PostMapping
    public ItemDto add(HttpServletRequest request, @RequestHeader("X-Sharer-User-Id") Long owner, @RequestBody ItemDto itemDto) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return itemServiceImpl.add(owner, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(HttpServletRequest request, @RequestHeader("X-Sharer-User-Id") Long owner,
                          @PathVariable("itemId") Long id, @RequestBody ItemDto itemDto) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return itemServiceImpl.update(id, owner, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemAndLastAndNextBookingDto getUser(HttpServletRequest request, @RequestHeader("X-Sharer-User-Id") Long owner, @PathVariable("itemId") Long id) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return itemServiceImpl.get(id, owner);
    }

    @GetMapping
    public List<ItemAndLastAndNextBookingDto> getAllItemtoUser(HttpServletRequest request, @RequestHeader("X-Sharer-User-Id") Long owner) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return itemServiceImpl.getAllItemtoUser(owner);
    }

    @GetMapping("/search")
    public List<ItemDto> getAllItemWithText(HttpServletRequest request, @RequestHeader("X-Sharer-User-Id") Long owner,
                                            @RequestParam(value = "text") String text) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return itemServiceImpl.getAllItemWithText(text, owner);
    }


    @PostMapping("/{itemId}/comment")
    public CommentDto add(HttpServletRequest request, @RequestHeader("X-Sharer-User-Id") Long owner, @PathVariable("itemId") Long id,
                          @RequestBody CommentDto commentDto) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return itemServiceImpl.addComment(owner, id, commentDto);
    }

}
