package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class RequestController {
    private static final String OWNER_ID_HEADER = "X-Sharer-User-Id";
    private final RequestClient requestClient;


    @PostMapping
    public ResponseEntity<Object> addRequest(
            @RequestHeader(OWNER_ID_HEADER) long userId,
            @RequestBody ItemRequestDto itemRequestDto) {
        validateEmptyDescription(itemRequestDto);
        log.info("Creating request {}, userId={}", itemRequestDto.getDescription(), userId);
        return requestClient.addRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getYourRequestsWithResponse(
            @RequestHeader(OWNER_ID_HEADER) long userId) {
        log.info("Get requests userId={}", userId);
        return requestClient.getYourRequestsWithResponse(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> listOfRequestsFromOtherUsers(
            @RequestHeader(OWNER_ID_HEADER) long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        validateParametersPagination(from, size);
        log.info("Get ALL requests other users {}, userId={}, from={}, size={}", userId, from, size);
        return requestClient.listOfRequestsFromOtherUsers(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestsWithResponse(
            @RequestHeader(OWNER_ID_HEADER) long userId,
            @PathVariable("requestId") long requestId) {
        log.info("Get request {}, userId={}", requestId, userId);
        return requestClient.getRequestsWithResponse(userId, requestId);
    }

    private void validateEmptyDescription(ItemRequestDto itemRequestDto) {
        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isBlank()) {
            log.info("The description cannot be empty");
            throw new ValidationException("The description cannot be empty");
        }
    }

    private void validateParametersPagination(Integer from, Integer size) {
        if (size == 0) {
            log.info("The parameters page is wrong size=0");
            throw new ValidationException("The parameters page is wrong size=0");
        }
        if ((from < 0 && size > 0) || (from >= 0 && size < 0) || (from < 0 && size < 0)) {
            log.info("The parameters page is wrong");
            throw new ValidationException("The parameters page is wrong from= " + from + ";   size= " + size);
        }
    }
}
