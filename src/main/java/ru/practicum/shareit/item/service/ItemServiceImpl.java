package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingNewNameIdDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemAndLastAndNextBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service("ItemServiceImpl")
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    private void validationItem(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            log.info("Название вещи не может быть пустым");
            throw new ValidationException("Название вещи не может быть пустым");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            log.info("Описание не может быть пустым");
            throw new ValidationException("Описание не может быть пустым");
        }
        if (itemDto.getAvailable() == null) {
            log.info("Статус аренды не может быть пустым");
            throw new ValidationException("Статус аренды не может быть пустым");
        }
    }

    private void validationIdOwner(Long owner) {
        if (userRepository.findUserById(owner) == null) {
            log.info("Нет такого идентификатора владельца");
            throw new NotFoundException(String.format("Нет такого идентификатора владельца № %s", owner));
        }
    }

    private void validationIdItem(Long id) {
        if (itemRepository.findItemById(id) == null) {
            log.info("Нет такого идентификатора");
            throw new NotFoundException(String.format("Нет такого идентификатора № %s", id));
        }
    }

    private void validationIdItemAndIdOwner(Long id, Long owner) {
        if (!itemRepository.findItemById(id).getOwner().equals(owner)) {
            log.info(String.format("У Владельца  № %s нет вещи с идентификатором  № %s", owner, id));
            throw new NotFoundException(String.format("У Владельца  № %s нет вещи с идентификатором  № %s", owner, id));
        }
    }


    @Transactional
    @Override
    public ItemDto add(Long owner, ItemDto itemDto) {
        validationItem(itemDto);
        validationIdOwner(owner);    // проверка наличия id владельца в БД
        Item item = ItemMapper.toItem(owner, itemDto);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional(readOnly = true)
    @Override
    public ItemAndLastAndNextBookingDto get(Long id, Long owner) {
        validationIdOwner(owner);
        validationIdItem(id);

        LocalDateTime time = LocalDateTime.now();
        Item item = itemRepository.findItemByIdAndOwner(id, owner);
        List<Booking> bookingsEnd = bookingRepository.findBookingsByItemAndEndBeforeOrderByEndDesc(item, time);
        List<Booking> bookingsStart = bookingRepository.findBookingsByItemAndStartAfterOrderByStartAsc(item, time);


        BookingNewNameIdDto lastBooking = null;
        BookingNewNameIdDto nextBooking = null;
        if (bookingsEnd != null && bookingsEnd.size() > 0) {
            lastBooking = new BookingNewNameIdDto();
            lastBooking.setId(bookingsEnd.get(0).getId());
            lastBooking.setBookerId(bookingsEnd.get(0).getBooker().getId());
        }
        if (bookingsStart != null && bookingsStart.size() > 0) {
            nextBooking = new BookingNewNameIdDto();
            nextBooking.setId(bookingsStart.get(0).getId());
            nextBooking.setBookerId(bookingsStart.get(0).getBooker().getId());
        }

        return ItemMapper.toItemAndLastAndNextBookingDto(itemRepository.getById(id), lastBooking, nextBooking);
    }

    @Transactional
    @Override
    public ItemDto update(Long id, Long owner, ItemDto itemDto) {
        validationIdOwner(owner);
        validationIdItem(id);
        validationIdItemAndIdOwner(id, owner);
        Item oldItem = itemRepository.getById(id);
        Item upItem = ItemMapper.toItem(owner, itemDto);

        upItem.setId(id);
        upItem.setName((upItem.getName() == null || upItem.getName().isBlank()) ? oldItem.getName() : upItem.getName());
        upItem.setDescription((upItem.getDescription() == null || upItem.getName().isBlank()) ? oldItem.getDescription() : upItem.getDescription());
        upItem.setAvailable(upItem.getAvailable() == null ? oldItem.getAvailable() : upItem.getAvailable());
        upItem.setRequest(upItem.getRequest() == null ? oldItem.getRequest() : upItem.getRequest());

        return ItemMapper.toItemDto(itemRepository.save(upItem));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemAndLastAndNextBookingDto> getAllItemtoUser(Long owner) {
        validationIdOwner(owner);
        List<ItemAndLastAndNextBookingDto> listBookings = new ArrayList<>();

        List<Item> items = itemRepository.findItemsByOwner(owner);
        for (Item item : items) {
            LocalDateTime time = LocalDateTime.now();
            List<Booking> bookingsEnd = bookingRepository.findBookingsByItemAndEndBeforeOrderByEndDesc(item, time);
            List<Booking> bookingsStart = bookingRepository.findBookingsByItemAndStartAfterOrderByStartAsc(item, time);

            BookingNewNameIdDto lastBooking = null;
            BookingNewNameIdDto nextBooking = null;
            if (bookingsEnd != null && bookingsEnd.size() > 0) {
                lastBooking = new BookingNewNameIdDto();
                lastBooking.setId(bookingsEnd.get(0).getId());
                lastBooking.setBookerId(bookingsEnd.get(0).getBooker().getId());
            }
            if (bookingsStart != null && bookingsStart.size() > 0) {
                nextBooking = new BookingNewNameIdDto();
                nextBooking.setId(bookingsStart.get(0).getId());
                nextBooking.setBookerId(bookingsStart.get(0).getBooker().getId());
            }
            listBookings.add(ItemMapper.toItemAndLastAndNextBookingDto(item, lastBooking, nextBooking));

        }
        return listBookings;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> getAllItemWithText(String text1, Long owner) {
        validationIdOwner(owner);
        if (text1.isBlank() || text1.isEmpty()) {
            return new ArrayList<>();
        }
        String text2 = text1;
        return itemRepository
                .findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(text1, text2).stream()
                .map(item -> ItemMapper.toItemDto(item))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentDto addComment(Long owner, Long id, CommentDto commentDto) {
        return null;
    }
}