package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemAndLastAndNextBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemServiceImpl;

    @Autowired
    public ItemController(@Qualifier("ItemServiceImpl") ItemService itemService) {
        this.itemServiceImpl = itemService;
    }


    @PostMapping
    public ItemDto add(HttpServletRequest request,
                       @RequestHeader("X-Sharer-User-Id") Long owner,
                       @RequestBody ItemDto itemDto) {
        final Long OWNER_CONST = owner;
        log.info("Request to the endpoint was received: '{} {}', string of request parameters: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return itemServiceImpl.add(OWNER_CONST, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(HttpServletRequest request,
                          @RequestHeader("X-Sharer-User-Id") Long owner,
                          @PathVariable("itemId") Long id,
                          @RequestBody ItemDto itemDto) {
        final Long OWNER_CONST = owner;
        log.info("Request to the endpoint was received: '{} {}', string of request parameters: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return itemServiceImpl.update(id, OWNER_CONST, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemAndLastAndNextBookingDto getUser(HttpServletRequest request,
                                                @RequestHeader("X-Sharer-User-Id") Long owner,
                                                @PathVariable("itemId") Long id) {
        final Long OWNER_CONST = owner;
        log.info("Request to the endpoint was received: '{} {}', string of request parameters: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return itemServiceImpl.get(id, OWNER_CONST);
    }

    @GetMapping
    public List<ItemAndLastAndNextBookingDto> getAllItemtoUser(HttpServletRequest request,
                                                               @RequestHeader("X-Sharer-User-Id") Long owner) {
        final Long OWNER_CONST = owner;
        log.info("Request to the endpoint was received: '{} {}', string of request parameters: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return itemServiceImpl.getAllItemtoUser(OWNER_CONST);
    }

    @GetMapping("/search")
    public List<ItemDto> getAllItemWithText(HttpServletRequest request,
                                            @RequestHeader("X-Sharer-User-Id") Long owner,
                                            @RequestParam(value = "text") String text) {
        final Long OWNER_CONST = owner;
        log.info("Request to the endpoint was received: '{} {}', string of request parameters: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return itemServiceImpl.getAllItemWithText(text, OWNER_CONST);
    }


    @PostMapping("/{itemId}/comment")
    public CommentDto add(HttpServletRequest request,
                          @RequestHeader("X-Sharer-User-Id") Long owner,
                          @PathVariable("itemId") Long id,
                          @RequestBody CommentDto commentDto) {
        final Long OWNER_CONST = owner;
        log.info("Request to the endpoint was received: '{} {}', string of request parameters: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return itemServiceImpl.addComment(OWNER_CONST, id, commentDto);
    }

}
