package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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

    private final BookingService bookingServiceImpl;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingServiceImpl = bookingService;
    }


    @PostMapping
    public BookingDto add(HttpServletRequest request,
                          @RequestHeader("X-Sharer-User-Id") Long owner,
                          @RequestBody RequestBookingDto requestBookingDto) {
        final Long OWNER_CONST = owner;
        log.info("Request to the endpoint was received: '{} {}', string of request parameters: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return bookingServiceImpl.add(OWNER_CONST, requestBookingDto, LocalDateTime.now());
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateStatus(HttpServletRequest request,
                                   @RequestHeader("X-Sharer-User-Id") Long owner,
                                   @PathVariable("bookingId") Long bookingId,
                                   @RequestParam(value = "approved") Boolean approved) {
        final Long OWNER_CONST = owner;
        log.info("Request to the endpoint was received: '{} {}', string of request parameters: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return bookingServiceImpl.update(OWNER_CONST, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(HttpServletRequest request,
                                     @RequestHeader("X-Sharer-User-Id") Long ownerOrBooker,
                                     @PathVariable("bookingId") Long id) {
        final Long OWNER_BOOKER_CONST = ownerOrBooker;
        log.info("Request to the endpoint was received: '{} {}', string of request parameters: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return bookingServiceImpl.getById(id, OWNER_BOOKER_CONST);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsByUserId(HttpServletRequest request,
                                                   @RequestHeader("X-Sharer-User-Id") Long owner,
                                                   @RequestParam(value = "state") Optional<String> state) {
        final Long OWNER_CONST = owner;
        log.info("Request to the endpoint was received: '{} {}', string of request parameters: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return bookingServiceImpl.getAllBookingsByUserId(OWNER_CONST, state.orElseGet(() -> "ALL"));
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsAllItemsByUserId(HttpServletRequest request,
                                                           @RequestHeader("X-Sharer-User-Id") Long owner,
                                                           @RequestParam(value = "state") Optional<String> state) {
        final Long OWNER_CONST = owner;
        log.info("Request to the endpoint was received: '{} {}', string of request parameters: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return bookingServiceImpl.getAllBookingsAllItemsByUserId(OWNER_CONST, state.orElseGet(() -> "ALL"));
    }
}