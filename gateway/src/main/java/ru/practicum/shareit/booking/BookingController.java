package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.StatusException;
import ru.practicum.shareit.exception.ValidationException;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private static final String OWNER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader(OWNER_ID_HEADER) long userId,
                                           @RequestBody BookItemRequestDto requestDto) {
        validateTime(requestDto, LocalDateTime.now());
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateStatus(@RequestHeader(OWNER_ID_HEADER) long userId,
                                               @PathVariable long bookingId,
                                               @RequestParam(value = "approved") Boolean approved) {
        log.info("Update booking {}, userId={}, approved={}", bookingId, userId, approved);
        return bookingClient.updateStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(OWNER_ID_HEADER) long userId,
                                             @PathVariable long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader(OWNER_ID_HEADER) long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new StatusException("Unknown state: UNSUPPORTED_STATUS"));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsAllItemsByUserId(@RequestHeader(OWNER_ID_HEADER) long userId,
                                                                 @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                                 @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new StatusException("Unknown state: UNSUPPORTED_STATUS"));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getAllBookingsAllItemsByUserId(userId, state, from, size);
    }


    private void validateTime(BookItemRequestDto requestDto, LocalDateTime time) {
        LocalDateTime start = requestDto.getStart();
        LocalDateTime end = requestDto.getEnd();
        if (start == null || end == null || start.isEqual(end) || end.isBefore(start) || start.isBefore(time) || end.isBefore(time)) {
            log.info("The time of booking is incorrectly indicated");
            throw new ValidationException("The time of booking is incorrectly indicated");
        }
    }
}
