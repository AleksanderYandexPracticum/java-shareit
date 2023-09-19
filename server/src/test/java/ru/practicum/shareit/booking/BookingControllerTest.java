package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;

import javax.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private BookingController bookingController;

    @Test
    void add() {
        Long owner = 1L;
        RequestBookingDto requestBookingDto = new RequestBookingDto(LocalDateTime.now(), LocalDateTime.now(), 1L);
        BookingDto bookingDto = BookingDto.builder()
                .id(null)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .item(new Item())
                .booker(null)
                .status(null)
                .build();

        when(bookingService.add(anyLong(), any(RequestBookingDto.class), any(LocalDateTime.class))).thenReturn(bookingDto);

        BookingDto actualItemDto = bookingController.add(request, owner, requestBookingDto);

        Assertions.assertEquals(bookingDto, actualItemDto);
    }

    @Test
    void updateStatus() {
        Long owner = 1L;
        Long bookingId = 1L;
        Boolean approved = true;
        BookingDto bookingDto = BookingDto.builder()
                .id(null)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .item(new Item())
                .booker(null)
                .status(null)
                .build();

        when(bookingService.update(owner, bookingId, approved)).thenReturn(bookingDto);

        BookingDto actualItemDto = bookingController.updateStatus(request, owner, bookingId, approved);

        Assertions.assertEquals(bookingDto, actualItemDto);
    }

    @Test
    void getBookingById() {
        Long ownerOrBooker = 1L;
        Long id = 1L;
        BookingDto bookingDto = BookingDto.builder()
                .id(null)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .item(new Item())
                .booker(null)
                .status(null)
                .build();

        when(bookingService.getById(id, ownerOrBooker)).thenReturn(bookingDto);

        BookingDto actualItemDto = bookingController.getBookingById(request, ownerOrBooker, id);

        Assertions.assertEquals(bookingDto, actualItemDto);
    }

    @Test
    void getAllBookingsByUserId() {
        Long owner = 1L;
        Long id = 1L;
        Optional<String> state = Optional.of("All");
        Integer from = 0;
        Integer size = 1;
        BookingDto bookingDto = BookingDto.builder()
                .id(null)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .item(new Item())
                .booker(null)
                .status(null)
                .build();
        List<BookingDto> list = List.of(bookingDto);

        when(bookingService.getAllBookingsByUserId(owner, state.orElseGet(() -> "ALL"), from, size)).thenReturn(list);

        List<BookingDto> actualItemDto = bookingController.getAllBookingsByUserId(request, owner, state, from, size);

        Assertions.assertEquals(bookingDto, actualItemDto.get(0));
    }

    @Test
    void getAllBookingsAllItemsByUserId() {
        Long owner = 1L;
        Long id = 1L;
        Optional<String> state = Optional.of("All");
        Integer from = 0;
        Integer size = 1;
        BookingDto bookingDto = BookingDto.builder()
                .id(null)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .item(new Item())
                .booker(null)
                .status(null)
                .build();
        List<BookingDto> list = List.of(bookingDto);

        when(bookingService.getAllBookingsAllItemsByUserId(owner, state.orElseGet(() -> "ALL"), from, size)).thenReturn(list);

        List<BookingDto> actualItemDto = bookingController.getAllBookingsAllItemsByUserId(request, owner, state, from, size);

        Assertions.assertEquals(bookingDto, actualItemDto.get(0));
    }
}