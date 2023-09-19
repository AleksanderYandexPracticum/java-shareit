package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/items")
@Validated
public class ItemController {
    private static final String OWNER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemClient itemClient;


    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(OWNER_ID_HEADER) long userId,
                                          @RequestBody ItemDto itemDto) {
        validateItem(itemDto);
        log.info("Creating item {}, userId={}", itemDto, userId);
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(OWNER_ID_HEADER) long userId,
                                         @PathVariable("itemId") long itemId,
                                         @RequestBody ItemDto itemDto) {
        log.info("Update item {}, userId={}", itemId, userId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getUser(@RequestHeader(OWNER_ID_HEADER) long userId,
                                          @PathVariable("itemId") long itemId) {
        log.info("Get item {}, userId={}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemtoUser(@RequestHeader(OWNER_ID_HEADER) long userId,
                                                   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get itemsToUser, userId={}, from={}, size={}", userId, from, size);
        return itemClient.getAllItemToUser(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getAllItemWithText(@RequestHeader(OWNER_ID_HEADER) long userId,
                                                     @RequestParam(value = "text") String text,
                                                     @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                     @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get allItemWithText, userId={}, text={}, from={}, size={}", userId, text, from, size);
        return itemClient.getAllItemWithText(userId, text, from, size);
    }


    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @RequestHeader(OWNER_ID_HEADER) Long userId,
            @PathVariable("itemId") long itemId,
            @RequestBody CommentDto commentDto) {
        validateComment(commentDto);
        log.info("Creating comment {}, userId={}, itemId={}", userId, itemId);
        return itemClient.addComment(userId, itemId, commentDto);
    }


    private void validateItem(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            log.info("The name of the item cannot be empty");
            throw new ValidationException("The name of the item cannot be empty");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            log.info("The description cannot be empty");
            throw new ValidationException("The description cannot be empty");
        }
        if (itemDto.getAvailable() == null) {
            log.info("The rental status cannot be empty");
            throw new ValidationException("The rental status cannot be empty");
        }
    }

    private void validateComment(CommentDto commentDto) {
        if (commentDto.getText() == null || commentDto.getText().isBlank()) {
            log.info("The comment cannot be empty");
            throw new ValidationException("The comment cannot be empty");
        }
    }
}
