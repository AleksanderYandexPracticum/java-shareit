package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingServiceImpl bookingServiceImpl;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingServiceImpl = (BookingServiceImpl) bookingService;
    }


    @PostMapping
    public BookingDto add(HttpServletRequest request, @RequestHeader("X-Sharer-User-Id") Long owner, @RequestBody RequestBookingDto requestBookingDto) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return bookingServiceImpl.add(owner, requestBookingDto, LocalDateTime.now());
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateStatus(HttpServletRequest request, @RequestHeader("X-Sharer-User-Id") Long owner,
                                              @PathVariable("bookingId") Long bookingId, @RequestParam(value = "approved") Boolean approved) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return bookingServiceImpl.update(owner, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(HttpServletRequest request, @RequestHeader("X-Sharer-User-Id") Long ownerOrBooker, @PathVariable("bookingId") Long id) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return bookingServiceImpl.getById(id, ownerOrBooker);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsByUserId(HttpServletRequest request,
                                                   @RequestHeader("X-Sharer-User-Id") Long owner,
                                                   @RequestParam(value = "state") Optional<String> state) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return bookingServiceImpl.getAllBookingsByUserId(owner, state.orElseGet(() -> "ALL"));
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsAllItemsByUserId(HttpServletRequest request,
                                                           @RequestHeader("X-Sharer-User-Id") Long owner,
                                                           @RequestParam(value = "state") Optional<String> state) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return bookingServiceImpl.getAllBookingsAllItemsByUserId(owner, state.orElseGet(() -> "ALL"));
    }
}