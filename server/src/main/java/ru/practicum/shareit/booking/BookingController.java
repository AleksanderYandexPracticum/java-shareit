package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String OWNER_ID_HEADER = "X-Sharer-User-Id";

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }


    @PostMapping
    public BookingDto add(HttpServletRequest request,
                          @RequestHeader(OWNER_ID_HEADER) Long owner,
                          @RequestBody RequestBookingDto requestBookingDto) {
        log.info("Request to the endpoint was received: '{} {}', string of request parameters: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return bookingService.add(owner, requestBookingDto, LocalDateTime.now());
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateStatus(HttpServletRequest request,
                                   @RequestHeader(OWNER_ID_HEADER) Long owner,
                                   @PathVariable Long bookingId,
                                   @RequestParam(value = "approved") Boolean approved) {
        log.info("Request to the endpoint was received: '{} {}', string of request parameters: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return bookingService.update(owner, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(HttpServletRequest request,
                                     @RequestHeader(OWNER_ID_HEADER) Long ownerOrBooker,
                                     @PathVariable("bookingId") Long id) {
        log.info("Request to the endpoint was received: '{} {}', string of request parameters: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return bookingService.getById(id, ownerOrBooker);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsByUserId(HttpServletRequest request,
                                                   @RequestHeader(OWNER_ID_HEADER) Long owner,
                                                   @RequestParam(value = "state") Optional<String> state,
                                                   @RequestParam(required = false, value = "from") Integer from,
                                                   @RequestParam(required = false, value = "size") Integer size) {
        log.info("Request to the endpoint was received: '{} {}', string of request parameters: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return bookingService.getAllBookingsByUserId(owner, state.orElseGet(() -> "ALL"), from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsAllItemsByUserId(HttpServletRequest request,
                                                           @RequestHeader(OWNER_ID_HEADER) Long owner,
                                                           @RequestParam(value = "state") Optional<String> state,
                                                           @RequestParam(required = false, value = "from") Integer from,
                                                           @RequestParam(required = false, value = "size") Integer size) {
        log.info("Request to the endpoint was received: '{} {}', string of request parameters: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return bookingService.getAllBookingsAllItemsByUserId(owner, state.orElseGet(() -> "ALL"), from, size);
    }
}