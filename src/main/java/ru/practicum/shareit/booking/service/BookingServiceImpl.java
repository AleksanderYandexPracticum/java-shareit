package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.StatusException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, ItemRepository itemRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    private void validationAvailable(Long itemId) {
        Item item = itemRepository.findItemById(itemId);
        if (item != null && !item.getAvailable()) {
            log.info("Вещь не доступна для бронирования");
            throw new ValidationException("Вещь не доступна для бронирования");
        }
    }

    private void validationOwner(Long owner) {
        if (userRepository.findUserById(owner) == null) {
            log.info("Нет такого идентификатора владельца");
            throw new NotFoundException("Нет такого идентификатора владельца");
        }
    }

    private void validationItemId(Long itemId) {
        if (itemRepository.findItemById(itemId) == null) {
            log.info("Такой вещи нет, недоступна для бронирования");
            throw new NotFoundException("Такой вещи нет, недоступна для бронирования");
        }
    }

    private void validationTime(RequestBookingDto requestBookingDto, LocalDateTime time) {
        LocalDateTime start = requestBookingDto.getStart();
        LocalDateTime end = requestBookingDto.getEnd();
        if (start == null || end == null || start.isEqual(end) || end.isBefore(start) || start.isBefore(time) || end.isBefore(time)) {
            log.info("Неправильно указано время бронирования");
            throw new ValidationException("Неправильно указано время бронирования");
        }
    }

    private void validationIdItemAndIdOwner(Long bookingId, Long owner) {
        if (!bookingRepository.findBookingById(bookingId).getItem().getOwner().equals(owner)) {
            log.info(String.format("Владельца  № %s не такой вещи " +
                    "- подтверждение бронирования не возможно", owner));
            throw new NotFoundException(String.format("У Владельца  № %s нет такой вещи " +
                    "- подтверждение бронирования не возможно", owner));
        }
    }

    private void validationOwnerOrBooker(Long id, Long ownerOrBooker) {     // Валидация автора бронирования и владельца
        Booking booking = bookingRepository.findBookingById(id);
        if (booking == null) {
            log.info("Нет такого идентификатора владельца или автора брони");
            throw new NotFoundException(String.format("Нет такого идентификатора владельца или автора брони № %s", ownerOrBooker));
        }

        Item item = itemRepository.findItemById(booking.getItem().getId());
        if (item == null || !booking.getBooker().getId().equals(ownerOrBooker) && !item.getOwner().equals(ownerOrBooker)) {
            log.info("Нет такого идентификатора владельца или автора брони");
            throw new NotFoundException(String.format("Нет такого идентификатора владельца или автора брони № %s", ownerOrBooker));
        }
    }

    private List<Item> validationIdOwnerHaveItem(Long owner) {
        List<Item> items = itemRepository.findItemsByOwner(owner);
        if (items == null || items.size() == 0) {
            log.info("Нет вещей у такого владельца");
            throw new NotFoundException(String.format("Нет вещей у такого идентификатора владельца № %s", owner));
        }
        return items;
    }

    private void validationState(String state) {
        List<Status> status = new ArrayList<>();
        Collections.addAll(status, Status.values());
        if (status.stream().map(s -> s.toString()).filter(s -> s.equals(state)).collect(Collectors.toList()).size() == 0) {
            log.info("Неправильно указан статус");
            throw new StatusException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private void validationApproved(Long bookingId) {
        if (bookingRepository.findBookingById(bookingId).getStatus().equals(Status.APPROVED)) {
            log.info("Approved уже есть");
            throw new ValidationException("Approved уже есть");
        }
    }

    private void validationSelfItem(Long owner, RequestBookingDto requestBookingDto) {     // Валидация, что вещь не принадлежит самому себе
        Item item = itemRepository.findItemById(requestBookingDto.getItemId());
        if (item.getOwner().equals(owner)) {
            log.info("Своя вещь");
            throw new NotFoundException(String.format("Это своя вещь № %s", requestBookingDto.getItemId()));
        }
    }

    @Transactional
    @Override
    public BookingDto add(Long owner, RequestBookingDto requestBookingDto, LocalDateTime time) {
        validationAvailable(requestBookingDto.getItemId()); // проверка доступности для бронирования
        validationItemId(requestBookingDto.getItemId());
        validationOwner(owner);
        validationTime(requestBookingDto, time);
        validationSelfItem(owner, requestBookingDto);

        Item item = itemRepository.findItemById(requestBookingDto.getItemId());
        User user = userRepository.findUserById(owner);

        Booking booking = BookingMapper.toBooking(item, user, requestBookingDto, Status.WAITING);
        BookingDto bookingDtoNew = BookingMapper.toBookingDto(bookingRepository.save(booking));
        return bookingDtoNew;
    }

    @Transactional
    @Override
    public BookingDto update(Long owner, Long bookingId, Boolean approved) {
        validationIdItemAndIdOwner(bookingId, owner);
        validationApproved(bookingId);

        Booking booking = bookingRepository.findBookingById(bookingId);
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);  /// Нужно ли добавить статус вещь занята available
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDto getById(Long id, Long ownerOrBooker) {
        validationOwnerOrBooker(id, ownerOrBooker);
        return BookingMapper.toBookingDto(bookingRepository.findBookingById(id));
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getAllBookingsByUserId(Long owner, String state) {
        validationIdOwnerHaveItem(owner);
        validationState(state);

        List<BookingDto> bookingDto = new ArrayList<>();
        LocalDateTime dateTime1 = LocalDateTime.now();
        LocalDateTime dateTime2 = dateTime1;
        User user = userRepository.findUserById(owner);

        if (state.equals("ALL")) {
            bookingDto = bookingRepository.getBookingsByBookerOrderByStartDesc(user).stream()
                    .map(booking -> BookingMapper.toBookingDto(booking))
                    .collect(Collectors.toList());
        }

        if (state.equals("WAITING")) {
            bookingDto = bookingRepository.getBookingByBookerAndStatusOrderByStartDesc(user, Status.WAITING).stream()
                    .map(booking -> BookingMapper.toBookingDto(booking))
                    .collect(Collectors.toList());
        }
        if (state.equals("REJECTED")) {
            bookingDto = bookingRepository.getBookingByBookerAndStatusOrderByStartDesc(user, Status.REJECTED).stream()
                    .map(booking -> BookingMapper.toBookingDto(booking))
                    .collect(Collectors.toList());
        }

        if (state.equals("CURRENT")) {
            bookingDto = bookingRepository.getBookingByBookerAndStartBeforeAndEndAfterOrderByStartDesc(user, dateTime1, dateTime2).stream()
                    .map(booking -> BookingMapper.toBookingDto(booking))
                    .collect(Collectors.toList());
        }
        if (state.equals("PAST")) {
            bookingDto = bookingRepository.getBookingByBookerAndEndBeforeOrderByStartDesc(user, dateTime1).stream()
                    .map(booking -> BookingMapper.toBookingDto(booking))
                    .collect(Collectors.toList());
        }
        if (state.equals("FUTURE")) {
            bookingDto = bookingRepository.getBookingByBookerAndStartAfterOrderByStartDesc(user, dateTime1).stream()
                    .map(booking -> BookingMapper.toBookingDto(booking))
                    .collect(Collectors.toList());
        }
        return bookingDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getAllBookingsAllItemsByUserId(Long owner, String state) {
        List<Long> itemIds = validationIdOwnerHaveItem(owner).stream()
                .map(item -> item.getId())
                .collect(Collectors.toList());
        validationState(state);

        List<BookingDto> bookingDto = new ArrayList<>();
        LocalDateTime dateTime1 = LocalDateTime.now();
        LocalDateTime dateTime2 = dateTime1;

        if (state.equals("ALL")) {
            bookingDto = bookingRepository.getBookingsByItemIdInOrderByStartDesc(itemIds).stream()
                    .map(booking -> BookingMapper.toBookingDto(booking))
                    .collect(Collectors.toList());
        }

        if (state.equals("WAITING")) {
            bookingDto = bookingRepository.getBookingByItemIdInAndStatusOrderByStartDesc(itemIds, Status.WAITING).stream()
                    .map(booking -> BookingMapper.toBookingDto(booking))
                    .collect(Collectors.toList());
        }
        if (state.equals("REJECTED")) {
            bookingDto = bookingRepository.getBookingByItemIdInAndStatusOrderByStartDesc(itemIds, Status.REJECTED).stream()
                    .map(booking -> BookingMapper.toBookingDto(booking))
                    .collect(Collectors.toList());
        }

        if (state.equals("CURRENT")) {
            bookingDto = bookingRepository.getBookingByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(itemIds, dateTime1, dateTime2).stream()
                    .map(booking -> BookingMapper.toBookingDto(booking))
                    .collect(Collectors.toList());
        }
        if (state.equals("PAST")) {
            bookingDto = bookingRepository.getBookingByItemIdInAndEndBeforeOrderByStartDesc(itemIds, dateTime1).stream()
                    .map(booking -> BookingMapper.toBookingDto(booking))
                    .collect(Collectors.toList());
        }
        if (state.equals("FUTURE")) {
            bookingDto = bookingRepository.getBookingByItemIdInAndStartAfterOrderByStartDesc(itemIds, dateTime1).stream()
                    .map(booking -> BookingMapper.toBookingDto(booking))
                    .collect(Collectors.toList());
        }
        return bookingDto;
    }
}