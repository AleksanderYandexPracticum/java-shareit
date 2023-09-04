package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    @Transactional
    @Override
    public BookingDto add(Long owner, RequestBookingDto requestBookingDto, LocalDateTime time) {
        validateAvailable(requestBookingDto.getItemId());
        validateItemId(requestBookingDto.getItemId());
        validateOwner(owner);
        validateTime(requestBookingDto, time);
        validateSelfItem(owner, requestBookingDto);

        Item item = itemRepository.findItemById(requestBookingDto.getItemId());
        User user = userRepository.findUserById(owner);

        Booking booking = BookingMapper.toBooking(item, user, requestBookingDto, Status.WAITING);
        BookingDto bookingDtoNew = BookingMapper.toBookingDto(bookingRepository.save(booking));
        return bookingDtoNew;
    }

    @Transactional
    @Override
    public BookingDto update(Long owner, Long bookingId, Boolean approved) {
        validateIdItemAndIdOwner(bookingId, owner);
        validateApproved(bookingId);

        Booking booking = bookingRepository.findBookingById(bookingId);
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDto getById(Long id, Long ownerOrBooker) {
        validateOwnerOrBooker(id, ownerOrBooker);
        return BookingMapper.toBookingDto(bookingRepository.findBookingById(id));
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getAllBookingsByUserId(Long owner, String state, Integer from, Integer size) {
        //validateIdOwnerHaveItem(owner);
        validateOwner(owner);
        validateState(state);

        List<BookingDto> bookingDto = new ArrayList<>();
        LocalDateTime dateTime1 = LocalDateTime.now();
        LocalDateTime dateTime2 = dateTime1;
        User user = userRepository.findUserById(owner);

        if (from != null && size != null) {
            validateParametersPagination(from, size);
            Sort sort = Sort.by(Sort.Direction.DESC, "start");
            Integer pageNumber = from / size;
            Pageable pageable = PageRequest.of(pageNumber, size, sort);

            if (state.equals("ALL")) {
                bookingDto = bookingRepository.getBookingsByBookerOrderByStartDesc(user, pageable).stream()
                        .map(booking -> BookingMapper.toBookingDto(booking))
                        .collect(Collectors.toList());
            } else if (state.equals("WAITING")) {
                bookingDto = bookingRepository.getBookingByBookerAndStatusOrderByStartDesc(user, Status.WAITING, pageable).stream()
                        .map(booking -> BookingMapper.toBookingDto(booking))
                        .collect(Collectors.toList());
            } else if (state.equals("REJECTED")) {
                bookingDto = bookingRepository.getBookingByBookerAndStatusOrderByStartDesc(user, Status.REJECTED, pageable).stream()
                        .map(booking -> BookingMapper.toBookingDto(booking))
                        .collect(Collectors.toList());
            } else if (state.equals("CURRENT")) {
                bookingDto = bookingRepository.getBookingByBookerAndStartBeforeAndEndAfterOrderByStartDesc(user, dateTime1, dateTime2, pageable).stream()
                        .map(booking -> BookingMapper.toBookingDto(booking))
                        .collect(Collectors.toList());
            } else if (state.equals("PAST")) {
                bookingDto = bookingRepository.getBookingByBookerAndEndBeforeOrderByStartDesc(user, dateTime1, pageable).stream()
                        .map(booking -> BookingMapper.toBookingDto(booking))
                        .collect(Collectors.toList());
            } else if (state.equals("FUTURE")) {
                bookingDto = bookingRepository.getBookingByBookerAndStartAfterOrderByStartDesc(user, dateTime1, pageable).stream()
                        .map(booking -> BookingMapper.toBookingDto(booking))
                        .collect(Collectors.toList());
            }
        } else {
            if (state.equals("ALL")) {
                bookingDto = bookingRepository.getBookingsByBookerOrderByStartDesc(user).stream()
                        .map(booking -> BookingMapper.toBookingDto(booking))
                        .collect(Collectors.toList());
            } else if (state.equals("WAITING")) {
                bookingDto = bookingRepository.getBookingByBookerAndStatusOrderByStartDesc(user, Status.WAITING).stream()
                        .map(booking -> BookingMapper.toBookingDto(booking))
                        .collect(Collectors.toList());
            } else if (state.equals("REJECTED")) {
                bookingDto = bookingRepository.getBookingByBookerAndStatusOrderByStartDesc(user, Status.REJECTED).stream()
                        .map(booking -> BookingMapper.toBookingDto(booking))
                        .collect(Collectors.toList());
            } else if (state.equals("CURRENT")) {
                bookingDto = bookingRepository.getBookingByBookerAndStartBeforeAndEndAfterOrderByStartDesc(user, dateTime1, dateTime2).stream()
                        .map(booking -> BookingMapper.toBookingDto(booking))
                        .collect(Collectors.toList());
            } else if (state.equals("PAST")) {
                bookingDto = bookingRepository.getBookingByBookerAndEndBeforeOrderByStartDesc(user, dateTime1).stream()
                        .map(booking -> BookingMapper.toBookingDto(booking))
                        .collect(Collectors.toList());
            } else if (state.equals("FUTURE")) {
                bookingDto = bookingRepository.getBookingByBookerAndStartAfterOrderByStartDesc(user, dateTime1).stream()
                        .map(booking -> BookingMapper.toBookingDto(booking))
                        .collect(Collectors.toList());
            }
        }

        return bookingDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getAllBookingsAllItemsByUserId(Long owner, String state, Integer from, Integer size) {
        List<Long> itemIds = validateIdOwnerHaveItem(owner).stream()
                .map(item -> item.getId())
                .collect(Collectors.toList());
        validateState(state);

        List<BookingDto> bookingDto = new ArrayList<>();
        LocalDateTime dateTime1 = LocalDateTime.now();
        LocalDateTime dateTime2 = dateTime1;

        if (from != null && size != null) {
            validateParametersPagination(from, size);
            Sort sort = Sort.by(Sort.Direction.DESC, "start");
            Integer pageNumber = from / size;
            Pageable pageable = PageRequest.of(pageNumber, size, sort);

            if (state.equals("ALL")) {
                bookingDto = bookingRepository.getBookingsByItemIdInOrderByStartDesc(itemIds, pageable).stream()
                        .map(booking -> BookingMapper.toBookingDto(booking))
                        .collect(Collectors.toList());
            } else if (state.equals("WAITING")) {
                bookingDto = bookingRepository.getBookingByItemIdInAndStatusOrderByStartDesc(itemIds, Status.WAITING, pageable).stream()
                        .map(booking -> BookingMapper.toBookingDto(booking))
                        .collect(Collectors.toList());
            } else if (state.equals("REJECTED")) {
                bookingDto = bookingRepository.getBookingByItemIdInAndStatusOrderByStartDesc(itemIds, Status.REJECTED, pageable).stream()
                        .map(booking -> BookingMapper.toBookingDto(booking))
                        .collect(Collectors.toList());
            } else if (state.equals("CURRENT")) {
                bookingDto = bookingRepository.getBookingByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(itemIds, dateTime1, dateTime2, pageable).stream()
                        .map(booking -> BookingMapper.toBookingDto(booking))
                        .collect(Collectors.toList());
            } else if (state.equals("PAST")) {
                bookingDto = bookingRepository.getBookingByItemIdInAndEndBeforeOrderByStartDesc(itemIds, dateTime1, pageable).stream()
                        .map(booking -> BookingMapper.toBookingDto(booking))
                        .collect(Collectors.toList());
            } else if (state.equals("FUTURE")) {
                bookingDto = bookingRepository.getBookingByItemIdInAndStartAfterOrderByStartDesc(itemIds, dateTime1, pageable).stream()
                        .map(booking -> BookingMapper.toBookingDto(booking))
                        .collect(Collectors.toList());
            }
        } else {
            if (state.equals("ALL")) {
                bookingDto = bookingRepository.getBookingsByItemIdInOrderByStartDesc(itemIds).stream()
                        .map(booking -> BookingMapper.toBookingDto(booking))
                        .collect(Collectors.toList());
            } else if (state.equals("WAITING")) {
                bookingDto = bookingRepository.getBookingByItemIdInAndStatusOrderByStartDesc(itemIds, Status.WAITING).stream()
                        .map(booking -> BookingMapper.toBookingDto(booking))
                        .collect(Collectors.toList());
            } else if (state.equals("REJECTED")) {
                bookingDto = bookingRepository.getBookingByItemIdInAndStatusOrderByStartDesc(itemIds, Status.REJECTED).stream()
                        .map(booking -> BookingMapper.toBookingDto(booking))
                        .collect(Collectors.toList());
            } else if (state.equals("CURRENT")) {
                bookingDto = bookingRepository.getBookingByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(itemIds, dateTime1, dateTime2).stream()
                        .map(booking -> BookingMapper.toBookingDto(booking))
                        .collect(Collectors.toList());
            } else if (state.equals("PAST")) {
                bookingDto = bookingRepository.getBookingByItemIdInAndEndBeforeOrderByStartDesc(itemIds, dateTime1).stream()
                        .map(booking -> BookingMapper.toBookingDto(booking))
                        .collect(Collectors.toList());
            } else if (state.equals("FUTURE")) {
                bookingDto = bookingRepository.getBookingByItemIdInAndStartAfterOrderByStartDesc(itemIds, dateTime1).stream()
                        .map(booking -> BookingMapper.toBookingDto(booking))
                        .collect(Collectors.toList());
            }

        }
        return bookingDto;
    }

    private void validateAvailable(Long itemId) {
        Item item = itemRepository.findItemById(itemId);
        if (item != null && !item.getAvailable()) {
            log.info("The item is not available for booking");
            throw new ValidationException("The item is not available for booking");
        }
    }

    private void validateOwner(Long owner) {
        if (!userRepository.existsById(owner)) {
            log.info("There is no such owner ID");
            throw new NotFoundException("There is no such owner ID");
        }
    }

    private void validateItemId(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            log.info("There is no such thing, it is not available for booking");
            throw new NotFoundException("There is no such thing, it is not available for booking");
        }
    }

    private void validateTime(RequestBookingDto requestBookingDto, LocalDateTime time) {
        LocalDateTime start = requestBookingDto.getStart();
        LocalDateTime end = requestBookingDto.getEnd();
        if (start == null || end == null || start.isEqual(end) || end.isBefore(start) || start.isBefore(time) || end.isBefore(time)) {
            log.info("The time of booking is incorrectly indicated");
            throw new ValidationException("The time of booking is incorrectly indicated");
        }
    }

    private void validateIdItemAndIdOwner(Long bookingId, Long owner) {
        if (!bookingRepository.findBookingById(bookingId).getItem().getOwner().equals(owner)) {
            log.info(String.format("Owner  № %s has no such thing " +
                    "- booking confirmation is not possible", owner));
            throw new NotFoundException(String.format("Owner  № %s has no such thing " +
                    "- booking confirmation is not possible", owner));
        }
    }

    private void validateOwnerOrBooker(Long id, Long ownerOrBooker) {     // Валидация автора бронирования и владельца
        Booking booking = bookingRepository.findBookingById(id);
        if (booking == null) {
            log.info("There is no such identifier of the owner or author of the reservation");
            throw new NotFoundException(String.format("There is no such identifier of the owner or author of the reservation № %s", ownerOrBooker));
        }

        Item item = itemRepository.findItemById(booking.getItem().getId());
        if (item == null || !booking.getBooker().getId().equals(ownerOrBooker) && !item.getOwner().equals(ownerOrBooker)) {
            log.info("There is no such identifier of the owner or author of the reservation");
            throw new NotFoundException(String.format("There is no such identifier of the owner or author of the reservation № %s", ownerOrBooker));
        }
    }

    private List<Item> validateIdOwnerHaveItem(Long owner) {
        List<Item> items = itemRepository.findItemsByOwner(owner);
        if (items == null || items.size() == 0) {
            log.info("There are no things from such an owner");
            throw new NotFoundException(String.format("There are no items with such an owner ID № %s", owner));
        }
        return items;
    }

    private void validateState(String state) {
        List<Status> status = new ArrayList<>();
        Collections.addAll(status, Status.values());
        if (status.stream().map(s -> s.toString()).filter(s -> s.equals(state)).collect(Collectors.toList()).size() == 0) {
            log.info("The status is incorrectly specified");
            throw new StatusException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private void validateApproved(Long bookingId) {
        if (bookingRepository.findBookingById(bookingId).getStatus().equals(Status.APPROVED)) {
            log.info("Approved already exists");
            throw new ValidationException("Approved already exists");
        }
    }

    private void validateSelfItem(Long owner, RequestBookingDto requestBookingDto) {
        Item item = itemRepository.findItemById(requestBookingDto.getItemId());
        if (item.getOwner().equals(owner)) {
            log.info("It's its own thing");
            throw new NotFoundException(String.format("It's its own thing № %s", requestBookingDto.getItemId()));
        }
    }

    private void validateParametersPagination(Integer from, Integer size) {
        if (size == 0) {
            log.info("The parameters page is wrong size=0");
            throw new ValidationException("The parameters page is wrong size=0");
        }
        if ((from < 0 && size > 0) || (from >= 0 && size < 0) || (from < 0 && size < 0)) {
            log.info("The parameters page is wrong");
            throw new ValidationException("The parameters page is wrong from= " + from + ";   size= " + size);
        }
    }
}