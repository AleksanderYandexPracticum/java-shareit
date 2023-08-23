package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long>, QuerydslPredicateExecutor<Booking> {

    Booking save(Booking booking);

    Booking findBookingById(Long id);

    List<Booking> getBookingsByBookerOrderByStartDesc(User owner);

    List<Booking> getBookingByBookerAndStatusOrderByStartDesc(User owner, Status status);

    List<Booking> getBookingByBookerAndStartBeforeAndEndAfterOrderByStartDesc(User owner, LocalDateTime dateTime1, LocalDateTime dateTime2);

    List<Booking> getBookingByBookerAndEndBeforeOrderByStartDesc(User owner, LocalDateTime dateTime1);

    List<Booking> getBookingByBookerAndStartAfterOrderByStartDesc(User owner, LocalDateTime dateTime1);


    List<Booking> getBookingsByItemIdInOrderByStartDesc(List<Long> itemsIds);

    List<Booking> getBookingByItemIdInAndStatusOrderByStartDesc(List<Long> itemsIds, Status status);

    List<Booking> getBookingByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(List<Long> itemsIds, LocalDateTime dateTime1, LocalDateTime dateTime2);

    List<Booking> getBookingByItemIdInAndEndBeforeOrderByStartDesc(List<Long> itemsIds, LocalDateTime dateTime1);

    List<Booking> getBookingByItemIdInAndStartAfterOrderByStartDesc(List<Long> itemsIds, LocalDateTime dateTime1);

    List<Booking> findBookingsByItemAndEndBeforeOrderByEndDesc(Item item, LocalDateTime time);

    List<Booking> findBookingsByItemAndStartBeforeAndEndAfterOrItemAndEndBeforeOrderByEndDesc(Item item, LocalDateTime time, LocalDateTime time1, Item item1, LocalDateTime time2);

    List<Booking> findBookingsByItemAndStatusInAndStartAfterOrderByStartAsc(Item item, List<Status> status, LocalDateTime time);

    // методы для комментариев
    List<Booking> findAllBookingByItemIdAndBookerIdAndStatusNotIn(Long id, Long owner, List<Status> status);

    List<Booking> findBookingByItemIdAndBookerIdAndEndBefore(Long id, Long owner, LocalDateTime time);

    Booking findBookingByItem(Item item);

}