package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
    private static final String OWNER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }


    @PostMapping
    public ItemDto add(HttpServletRequest request,
                       @RequestHeader(OWNER_ID_HEADER) Long owner,
                       @RequestBody ItemDto itemDto) {
        log.info("Request to the endpoint was received: '{} {}', string of request parameters: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return itemService.add(owner, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(HttpServletRequest request,
                          @RequestHeader(OWNER_ID_HEADER) Long owner,
                          @PathVariable("itemId") Long id,
                          @RequestBody ItemDto itemDto) {
        log.info("Request to the endpoint was received: '{} {}', string of request parameters: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return itemService.update(id, owner, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemAndLastAndNextBookingDto getUser(HttpServletRequest request,
                                                @RequestHeader(OWNER_ID_HEADER) Long owner,
                                                @PathVariable("itemId") Long id) {
        log.info("Request to the endpoint was received: '{} {}', string of request parameters: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return itemService.get(id, owner);
    }

    @GetMapping
    public List<ItemAndLastAndNextBookingDto> getAllItemtoUser(HttpServletRequest request,
                                                               @RequestHeader(OWNER_ID_HEADER) Long owner) {
        log.info("Request to the endpoint was received: '{} {}', string of request parameters: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return itemService.getAllItemtoUser(owner);
    }

    @GetMapping("/search")
    public List<ItemDto> getAllItemWithText(HttpServletRequest request,
                                            @RequestHeader(OWNER_ID_HEADER) Long owner,
                                            @RequestParam(value = "text") String text) {
        log.info("Request to the endpoint was received: '{} {}', string of request parameters: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return itemService.getAllItemWithText(text, owner);
    }


    @PostMapping("/{itemId}/comment")
    public CommentDto add(HttpServletRequest request,
                          @RequestHeader(OWNER_ID_HEADER) Long owner,
                          @PathVariable("itemId") Long id,
                          @RequestBody CommentDto commentDto) {
        log.info("Request to the endpoint was received: '{} {}', string of request parameters: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return itemService.addComment(owner, id, commentDto);
    }

}
