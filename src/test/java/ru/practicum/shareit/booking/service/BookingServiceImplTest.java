package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {


    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    BookingServiceImpl bookingServiceimpl;

    @Captor
    private ArgumentCaptor<Booking> bookingArgumentCaptor;

    @Test
    void add() {
        Long owner = 2L;
        RequestBookingDto requestBookingDto = new RequestBookingDto(
                LocalDateTime.now().plusDays(1L),
                LocalDateTime.now().plusDays(2L),
                1L);
        Item item = new Item(
                null, "Топор", "тупой", true, 1L, 1L);
        User user = new User("Boris", "bJ@mail.ru");
        user.setId(1L);
        Booking booking = new Booking();

        when(itemRepository.findItemById(any())).thenReturn(item);
        when(itemRepository.existsById(any())).thenReturn(true);
        when(userRepository.existsById(any())).thenReturn(true);

        when(itemRepository.findItemById(any())).thenReturn(item);

        when(userRepository.findUserById(any())).thenReturn(user);

        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto bookingDto = bookingServiceimpl.add(owner, requestBookingDto, LocalDateTime.now());

        verify(bookingRepository).save(bookingArgumentCaptor.capture());
        Booking saveBooking = bookingArgumentCaptor.getValue();

        assertEquals(item, saveBooking.getItem());
        assertEquals(user, saveBooking.getBooker());

        item.setAvailable(false);
        when(itemRepository.findItemById(any())).thenReturn(item);
        assertThrows(ValidationException.class, () -> bookingServiceimpl.add(owner, requestBookingDto, LocalDateTime.now()));

        item.setAvailable(true);
        when(itemRepository.existsById(any())).thenReturn(false);
        assertThrows(NotFoundException.class, () -> bookingServiceimpl.add(owner, requestBookingDto, LocalDateTime.now()));

        when(itemRepository.existsById(any())).thenReturn(true);
        when(userRepository.existsById(any())).thenReturn(false);
        assertThrows(NotFoundException.class, () -> bookingServiceimpl.add(owner, requestBookingDto, LocalDateTime.now()));

        when(userRepository.existsById(any())).thenReturn(true);
        requestBookingDto.setStart(LocalDateTime.now().minusDays(3L));
        assertThrows(ValidationException.class, () -> bookingServiceimpl.add(owner, requestBookingDto, LocalDateTime.now()));

        requestBookingDto.setStart(LocalDateTime.now());
        assertThrows(NotFoundException.class, () -> bookingServiceimpl.add(1L, requestBookingDto, LocalDateTime.now()));

    }

    @Test
    void update() {
        Long owner = 1L;
        Long bookingId = 1L;
        Boolean approved = true;

        Item item = new Item(
                null, "Топор", "тупой", true, 1L, 1L);
        User user = new User("Boris", "bJ@mail.ru");
        user.setId(1L);
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1L))
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build();

        when(bookingRepository.findBookingById(any())).thenReturn(booking);

        when(bookingRepository.save(any())).thenReturn(booking);

        bookingServiceimpl.update(owner, bookingId, approved);

        verify(bookingRepository).save(bookingArgumentCaptor.capture());
        Booking saveBooking = bookingArgumentCaptor.getValue();
        assertEquals(item, saveBooking.getItem());
        assertEquals(user, saveBooking.getBooker());
        assertEquals(Status.APPROVED, saveBooking.getStatus());


        assertThrows(NotFoundException.class, () -> bookingServiceimpl.update(2L, bookingId, approved));
        booking.setStatus(Status.APPROVED);
        assertThrows(ValidationException.class, () -> bookingServiceimpl.update(owner, bookingId, approved));

    }

    @Test
    void getById() {
        Long id = 1L;
        Long  ownerOrBooker = 1L;

        Item item = new Item(
                null, "Топор", "тупой", true, 2L, 1L);
        User user = new User("Boris", "bJ@mail.ru");
        user.setId(1L);
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1L))
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build();
        when(bookingRepository.findBookingById(any())).thenReturn(booking);

        when(itemRepository.findItemById(any())).thenReturn(item);

        BookingDto bookingDto = bookingServiceimpl.getById(id, ownerOrBooker);
        assertEquals(item, bookingDto.getItem());
        assertEquals(user, bookingDto.getBooker());

        when(bookingRepository.findBookingById(any())).thenReturn(null);
        assertThrows(NotFoundException.class, () -> bookingServiceimpl.getById(id, ownerOrBooker));

        when(bookingRepository.findBookingById(any())).thenReturn(booking);
        when(itemRepository.findItemById(any())).thenReturn(null);
        assertThrows(NotFoundException.class, () -> bookingServiceimpl.getById(id, ownerOrBooker));

        when(itemRepository.findItemById(any())).thenReturn(item);

        booking.getBooker().setId(2L);
        assertThrows(NotFoundException.class, () -> bookingServiceimpl.getById(id, ownerOrBooker));

    }

    @Test
    void getAllBookingsByUserId() {
        Long owner = 1L;
        Long bookingId = 1L;
        Boolean approved = true;

        RequestBookingDto requestBookingDto = new RequestBookingDto(
                LocalDateTime.now().plusDays(1L),
                LocalDateTime.now().plusDays(2L),
                1L);
        Item item = new Item(
                null, "Топор", "тупой", true, 1L, 1L);
        User user = new User("Boris", "bJ@mail.ru");
        user.setId(1L);
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1L))
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build();








    }

    @Test
    void getAllBookingsAllItemsByUserId() {
    }
}