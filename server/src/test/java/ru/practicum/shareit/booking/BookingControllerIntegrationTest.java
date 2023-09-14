package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.StatusException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = BookingController.class)
class BookingControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;


    @SneakyThrows
    @Test
    void add() {
        BookingDto bookingDto = BookingDto.builder()
                .id(null)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1L))
                .item(new Item())
                .booker(new User())
                .status(Status.WAITING)
                .build();
        when(bookingService.add(any(), any(), any())).thenReturn(bookingDto);

        String result = mockMvc.perform(post("/bookings")
                .header("X-Sharer-User-Id", 1L)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertEquals(objectMapper.writeValueAsString(bookingDto), result);

    }

    @SneakyThrows
    @Test
    void updateStatus() {
        Long bookingId = 1L;
        BookingDto bookingDto = BookingDto.builder()
                .id(null)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1L))
                .item(new Item())
                .booker(new User())
                .status(Status.WAITING)
                .build();
        when(bookingService.update(any(), any(), any())).thenReturn(bookingDto);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                .param("approved", "true")
                .header("X-Sharer-User-Id", 1L)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertEquals(objectMapper.writeValueAsString(bookingDto), result);

    }

    @SneakyThrows
    @Test
    void getBookingById() {

        Long id = 1L;
        Long ownerOrBooker = 1L;
        BookingDto bookingDto = BookingDto.builder()
                .id(null)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1L))
                .item(new Item())
                .booker(new User())
                .status(Status.WAITING)
                .build();
        when(bookingService.getById(any(), any())).thenReturn(bookingDto);
        mockMvc.perform(get("/bookings/{bookingId}", id)
                .header("X-Sharer-User-Id", 1L))
                .andDo(print())
                .andExpect(status().isOk());

        verify(bookingService).getById(id, ownerOrBooker);

    }

    @SneakyThrows
    @Test
    void getAllBookingsByUserId() {
        Long owner = 1L;
        Integer from = 0;
        Integer size = 1;

        BookingDto bookingDto = BookingDto.builder()
                .id(null)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1L))
                .item(new Item())
                .booker(new User())
                .status(Status.WAITING)
                .build();
        List<BookingDto> list = List.of(bookingDto);

        when(bookingService.getAllBookingsByUserId(any(), any(), any(), any())).thenReturn(list);
        mockMvc.perform(get("/bookings")
                .param("state", "All")
                .param("from", "0")
                .param("size", "1")
                .header("X-Sharer-User-Id", 1L))
                .andDo(print())
                .andExpect(status().isOk());

        verify(bookingService).getAllBookingsByUserId(owner, "All", from, size);

    }

    @SneakyThrows
    @Test
    void getAllBookingsAllItemsByUserId() {
        Long owner = 1L;
        Integer from = 0;
        Integer size = 1;

        BookingDto bookingDto = BookingDto.builder()
                .id(null)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1L))
                .item(new Item())
                .booker(new User())
                .status(Status.WAITING)
                .build();
        List<BookingDto> list = List.of(bookingDto);

        when(bookingService.getAllBookingsAllItemsByUserId(any(), any(), any(), any())).thenReturn(list);
        mockMvc.perform(get("/bookings/owner")
                .param("state", "All")
                .param("from", "0")
                .param("size", "1")
                .header("X-Sharer-User-Id", 1L))
                .andDo(print())
                .andExpect(status().isOk());

        verify(bookingService).getAllBookingsAllItemsByUserId(owner, "All", from, size);
    }

    @SneakyThrows
    @Test
    void validateException() {
        BookingDto bookingDto = BookingDto.builder()
                .id(null)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1L))
                .item(new Item())
                .booker(new User())
                .status(Status.WAITING)
                .build();

        when(bookingService.add(any(), any(), any())).thenThrow(new ValidationException("The item is not available for booking"));
        mockMvc.perform(post("/bookings")
                .header("X-Sharer-User-Id", 1L)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(bookingDto)))
                .andDo(print())
                .andExpect(status().is(400))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ValidationException))
                .andExpect(result -> assertEquals("The item is not available for booking", result.getResolvedException().getMessage()));

    }

    @SneakyThrows
    @Test
    void validateNotFoundAndStatusException() {
        BookingDto bookingDto = BookingDto.builder()
                .id(null)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1L))
                .item(new Item())
                .booker(new User())
                .status(Status.WAITING)
                .build();

        when(bookingService.add(any(), any(), any())).thenThrow(new NotFoundException("There is no such thing, it is not available for booking"));
        mockMvc.perform(post("/bookings")
                .header("X-Sharer-User-Id", 1L)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(bookingDto)))
                .andDo(print())
                .andExpect(status().is(404))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException))
                .andExpect(result -> assertEquals("There is no such thing, it is not available for booking", result.getResolvedException().getMessage()));

        when(bookingService.getAllBookingsByUserId(any(), any(), any(), any())).thenThrow(new StatusException("Unknown state: UNSUPPORTED_STATUS"));
        mockMvc.perform(get("/bookings")
                .param("state", "All")
                .param("from", "0")
                .param("size", "1")
                .header("X-Sharer-User-Id", 1L))
                .andDo(print())
                .andExpect(status().is(400))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof StatusException))
                .andExpect(result -> assertEquals("Unknown state: UNSUPPORTED_STATUS", result.getResolvedException().getMessage()));
    }
}