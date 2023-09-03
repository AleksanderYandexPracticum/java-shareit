package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryIT {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    private Long user1Id;
    private Long user2Id;

    private User returnUser1;
    private User returnUser2;
    private Item returnItem1;
    private Item returnItem2;
    private Booking returnBooking1;
    private Booking returnBooking2;
    Integer from = 0;
    Integer size = 2;
    Integer pageNumber = from / size;
    Sort sort = Sort.by(Sort.Direction.DESC, "start");
    Pageable pageable = PageRequest.of(pageNumber, size, sort);

    @BeforeEach
    private void addUser() {
        User user1 = new User("Jon", "jon@mail.ru");
        User user2 = new User("Piter", "pit@yandex.ru");
        this.returnUser1 = userRepository.save(user1);
        this.returnUser2 = userRepository.save(user2);

        this.user1Id = returnUser1.getId();
        this.user2Id = returnUser2.getId();

        Item item1 = Item.builder()
                .name("молоток")
                .description("маленький")
                .available(true)
                .owner(user1Id)
                .requestId(null)
                .build();
        Item item2 = Item.builder()
                .name("топор")
                .description("большой")
                .available(true)
                .owner(user2Id)
                .requestId(null)
                .build();
        this.returnItem1 = itemRepository.save(item1);
        this.returnItem2 = itemRepository.save(item2);

        Booking booking1 = Booking.builder()
                .start(LocalDateTime.now().plusDays(1L))
                .end(LocalDateTime.now().plusDays(2L))
                .item(item1)
                .booker(user1)
                .status(Status.WAITING)
                .build();
        Booking booking2 = Booking.builder()
                .start(LocalDateTime.now().plusDays(3L))
                .end(LocalDateTime.now().plusDays(4L))
                .item(item2)
                .booker(user2)
                .status(Status.WAITING)
                .build();
        this.returnBooking1 = bookingRepository.save(booking1);
        this.returnBooking2 = bookingRepository.save(booking2);
    }

    @AfterEach
    private void deleteUsers() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    void save() {
        Booking booking3 = Booking.builder()
                .start(LocalDateTime.now().plusDays(6L))
                .end(LocalDateTime.now().plusDays(7L))
                .item(returnItem2)
                .booker(returnUser2)
                .status(Status.WAITING)
                .build();
        Booking actualBooking = bookingRepository.save(booking3);
        assertEquals(actualBooking, booking3);
    }

    @Test
    void findBookingById() {

        Booking actualBooking = bookingRepository.findBookingById(returnBooking1.getId());
        assertEquals(actualBooking, returnBooking1);
    }

    @Test
    void getBookingsByBookerOrderByStartDesc() {

        List<Booking> list = bookingRepository.getBookingsByBookerOrderByStartDesc(returnUser1);
        assertTrue(list.size() == 1);
        assertEquals(list.get(0), returnBooking1);
    }

    @Test
    void getBookingByBookerAndStatusOrderByStartDesc() {
        List<Booking> list = bookingRepository.getBookingByBookerAndStatusOrderByStartDesc(returnUser1, Status.WAITING);
        assertTrue(list.size() == 1);
        assertEquals(list.get(0), returnBooking1);
    }

    @Test
    void getBookingByBookerAndStartBeforeAndEndAfterOrderByStartDesc() {
        List<Booking> list = bookingRepository.getBookingByBookerAndStartBeforeAndEndAfterOrderByStartDesc(returnUser1, LocalDateTime.now().plusDays(1L), LocalDateTime.now().plusDays(1L));
        assertTrue(list.size() == 1);
        assertEquals(list.get(0), returnBooking1);
    }

    @Test
    void getBookingByBookerAndEndBeforeOrderByStartDesc() {
        List<Booking> list = bookingRepository.getBookingByBookerAndEndBeforeOrderByStartDesc(returnUser1, LocalDateTime.now().plusDays(6L));
        assertTrue(list.size() == 1);
        assertEquals(list.get(0), returnBooking1);
    }

    @Test
    void getBookingByBookerAndStartAfterOrderByStartDesc() {
        List<Booking> list = bookingRepository.getBookingByBookerAndStartAfterOrderByStartDesc(returnUser1, LocalDateTime.now().minusDays(1L));
        assertTrue(list.size() == 1);
        assertEquals(list.get(0), returnBooking1);
    }

    @Test
    void getBookingsByItemIdInOrderByStartDesc() {
        List<Long> itemsIds = List.of(returnItem1.getId(), returnItem2.getId());
        List<Booking> list = bookingRepository.getBookingsByItemIdInOrderByStartDesc(itemsIds);
        assertTrue(list.size() == 2);
        assertEquals(list.get(1), returnBooking1);
        assertEquals(list.get(0), returnBooking2);
    }

    @Test
    void getBookingByItemIdInAndStatusOrderByStartDesc() {
        List<Long> itemsIds = List.of(returnItem1.getId(), returnItem2.getId());
        List<Booking> list = bookingRepository.getBookingByItemIdInAndStatusOrderByStartDesc(itemsIds, Status.WAITING);
        assertTrue(list.size() == 2);
        assertEquals(list.get(1), returnBooking1);
        assertEquals(list.get(0), returnBooking2);
    }

    @Test
    void getBookingByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc() {
        List<Long> itemsIds = List.of(returnItem1.getId(), returnItem2.getId());
        List<Booking> list = bookingRepository.getBookingByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(itemsIds, LocalDateTime.now().plusDays(1L), LocalDateTime.now().plusDays(1L));
        assertTrue(list.size() == 1);
        assertEquals(list.get(0), returnBooking1);

    }

    @Test
    void getBookingByItemIdInAndEndBeforeOrderByStartDesc() {
        List<Long> itemsIds = List.of(returnItem1.getId(), returnItem2.getId());
        List<Booking> list = bookingRepository.getBookingByItemIdInAndEndBeforeOrderByStartDesc(itemsIds, LocalDateTime.now().plusDays(2L));
        assertTrue(list.size() == 1);
        assertEquals(list.get(0), returnBooking1);
    }

    @Test
    void getBookingByItemIdInAndStartAfterOrderByStartDesc() {
        List<Long> itemsIds = List.of(returnItem1.getId(), returnItem2.getId());
        List<Booking> list = bookingRepository.getBookingByItemIdInAndStartAfterOrderByStartDesc(itemsIds, LocalDateTime.now().minusDays(1L));
        assertTrue(list.size() == 2);
        assertEquals(list.get(0), returnBooking2);
    }

    @Test
    void findBookingsByItemAndEndBeforeOrderByEndDesc() {
        List<Booking> list = bookingRepository.findBookingsByItemAndEndBeforeOrderByEndDesc(returnItem1, LocalDateTime.now().plusDays(3L));
        assertTrue(list.size() == 1);
        assertEquals(list.get(0), returnBooking1);
    }

    @Test
    void findBookingsByItemAndStatusInAndStartAfterOrderByStartAsc() {
        List<Status> status = List.of(Status.APPROVED, Status.CURRENT, Status.PAST);
        List<Booking> list = bookingRepository.findBookingsByItemAndStatusInAndStartAfterOrderByStartAsc(returnItem1, status, LocalDateTime.now().minusDays(1L));
        assertTrue(list.size() == 0);
    }

    @Test
    void findAllBookingByItemIdAndBookerIdAndStatusNotIn() {
        List<Status> status = List.of(Status.REJECTED, Status.CANCELED, Status.WAITING, Status.FUTURE);
        List<Booking> list = bookingRepository.findAllBookingByItemIdAndBookerIdAndStatusNotIn(returnItem1.getId(), returnUser1.getId(), status);
        assertTrue(list.size() == 0);
    }

    @Test
    void findBookingByItemIdAndBookerIdAndEndBefore() {
        List<Booking> list = bookingRepository.findBookingByItemIdAndBookerIdAndEndBefore(returnItem1.getId(), returnUser1.getId(), LocalDateTime.now().plusDays(3L));
        assertTrue(list.size() == 1);
    }

    @Test
    void findBookingByItem() {
        Booking actualBooking = bookingRepository.findBookingByItem(returnItem1);
        assertEquals(actualBooking, returnBooking1);
    }

    @Test
    void testGetBookingsByBookerOrderByStartDesc() {
        List<Booking> list = bookingRepository.getBookingsByBookerOrderByStartDesc(returnUser1, pageable);
        assertTrue(list.size() == 1);
        assertEquals(list.get(0), returnBooking1);
    }

    @Test
    void testGetBookingByBookerAndStatusOrderByStartDesc() {
        List<Booking> list = bookingRepository.getBookingByBookerAndStatusOrderByStartDesc(returnUser1, Status.WAITING, pageable);
        assertTrue(list.size() == 1);
        assertEquals(list.get(0), returnBooking1);
    }

    @Test
    void testGetBookingByBookerAndStartBeforeAndEndAfterOrderByStartDesc() {
        List<Booking> list = bookingRepository.getBookingByBookerAndStartBeforeAndEndAfterOrderByStartDesc(
                returnUser1,
                LocalDateTime.now().plusDays(1L),
                LocalDateTime.now().plusDays(1L),
                pageable);
        assertTrue(list.size() == 1);
        assertEquals(list.get(0), returnBooking1);
    }

    @Test
    void testGetBookingByBookerAndEndBeforeOrderByStartDesc() {
        List<Booking> list = bookingRepository.getBookingByBookerAndEndBeforeOrderByStartDesc(
                returnUser1,
                LocalDateTime.now().plusDays(2L),
                pageable);
        assertTrue(list.size() == 1);
        assertEquals(list.get(0), returnBooking1);
    }

    @Test
    void testGetBookingByBookerAndStartAfterOrderByStartDesc() {
        List<Booking> list = bookingRepository.getBookingByBookerAndStartAfterOrderByStartDesc(
                returnUser1,
                LocalDateTime.now().minusDays(1L),
                pageable);
        assertTrue(list.size() == 1);
        assertEquals(list.get(0), returnBooking1);
    }

    @Test
    void testGetBookingsByItemIdInOrderByStartDesc() {
        List<Long> itemsIds = List.of(returnItem1.getId(), returnItem2.getId());
        List<Booking> list = bookingRepository.getBookingsByItemIdInOrderByStartDesc(itemsIds, pageable);
        assertTrue(list.size() == 2);
        assertEquals(list.get(1), returnBooking1);
        assertEquals(list.get(0), returnBooking2);
    }

    @Test
    void testGetBookingByItemIdInAndStatusOrderByStartDesc() {
        List<Long> itemsIds = List.of(returnItem1.getId(), returnItem2.getId());
        List<Booking> list = bookingRepository.getBookingByItemIdInAndStatusOrderByStartDesc(itemsIds, Status.WAITING, pageable);
        assertTrue(list.size() == 2);
        assertEquals(list.get(1), returnBooking1);
        assertEquals(list.get(0), returnBooking2);
    }

    @Test
    void testGetBookingByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc() {
        List<Long> itemsIds = List.of(returnItem1.getId(), returnItem2.getId());
        List<Booking> list = bookingRepository.getBookingByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(
                itemsIds,
                LocalDateTime.now().plusDays(1L),
                LocalDateTime.now().plusDays(1L),
                pageable);
        assertTrue(list.size() == 1);
        assertEquals(list.get(0), returnBooking1);
    }

    @Test
    void testGetBookingByItemIdInAndEndBeforeOrderByStartDesc() {
        List<Long> itemsIds = List.of(returnItem1.getId(), returnItem2.getId());
        List<Booking> list = bookingRepository.getBookingByItemIdInAndEndBeforeOrderByStartDesc(
                itemsIds,
                LocalDateTime.now().plusDays(3L),
                pageable);
        assertTrue(list.size() == 1);
        assertEquals(list.get(0), returnBooking1);
    }

    @Test
    void testGetBookingByItemIdInAndStartAfterOrderByStartDesc() {
        List<Long> itemsIds = List.of(returnItem1.getId(), returnItem2.getId());
        List<Booking> list = bookingRepository.getBookingByItemIdInAndStartAfterOrderByStartDesc(
                itemsIds,
                LocalDateTime.now().minusDays(1),
                pageable);
        assertTrue(list.size() == 2);
        assertEquals(list.get(0), returnBooking2);
        assertEquals(list.get(1), returnBooking1);
    }

    @Test
    void getBookingsByBooker() {
        List<Booking> list = bookingRepository.getBookingsByBooker(returnUser2, pageable);
        assertTrue(list.size() == 1);
        assertEquals(list.get(0), returnBooking2);
    }
}