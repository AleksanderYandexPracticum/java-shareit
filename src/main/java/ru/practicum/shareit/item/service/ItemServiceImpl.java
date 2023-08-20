package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingNewNameIdDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentCreatedStringDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemAndLastAndNextBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service("ItemServiceImpl")
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository,
                           BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
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

    private void validationIdOwnerHaveBookingItem(Long owner, Long id) {
        LocalDateTime time = LocalDateTime.now();
        List<Status> status = Arrays.asList(Status.REJECTED, Status.CANCELED, Status.WAITING, Status.FUTURE);
        if (bookingRepository.findAllBookingByItemIdAndBookerIdAndStatusNotIn(id, owner, status).size() == 0
                || bookingRepository.findBookingByItemIdAndBookerIdAndEndBefore(id, owner, time).size() == 0) {
            log.info("Отзыв может оставить только тот пользователь, " +
                    "который брал эту вещь в аренду, и только после окончания срока аренды");
            throw new ValidationException(String.format("Отзыв может оставить только тот пользователь, " +
                    "который брал эту вещь в аренду, и только после окончания срока аренды " +
                    "Владелец  № %s  вещь с идентификатором  № %s", owner, id));
        }
    }

    private void validationComment(CommentDto commentDto) {
        if (commentDto.getText() == null || commentDto.getText().isBlank()) {
            log.info("Комментарий не может быть пустым");
            throw new ValidationException("Комментарий не может быть пустым");
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

    @Transactional()
    @Override
    public ItemAndLastAndNextBookingDto get(Long id, Long owner) {
        validationIdOwner(owner);
        validationIdItem(id);

        LocalDateTime time = LocalDateTime.now();
        BookingNewNameIdDto lastBooking = null;
        BookingNewNameIdDto nextBooking = null;

        Item item = itemRepository.getItemByIdAndOwner(id, owner);
        if (item != null) {  //Это если не владелец вещи
            List<Booking> bookingsEnd = bookingRepository.findBookingsByItemAndStartBeforeAndEndAfterOrItemAndEndBeforeOrderByEndDesc(item, time, time, item, time);

            List<Status> status = Arrays.asList(Status.APPROVED, Status.CURRENT, Status.PAST);
            List<Booking> bookingsStart = bookingRepository.findBookingsByItemAndStatusInAndStartAfterOrderByStartAsc(item, status, time);


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
        }

        item = itemRepository.findItemById(id);

        List<CommentCreatedStringDto> commentsCreatedStringDto = new ArrayList<>();

        for (Comment comment : commentRepository.getCommentsByItem(item)) {
            CommentCreatedStringDto commentCreatedStringDto = CommentMapper.toCommentCreatedStringDto(comment);

            commentCreatedStringDto.setAuthorName(comment.getAuthor().getName());
            commentsCreatedStringDto.add(commentCreatedStringDto);
        }


        return ItemMapper.toItemAndLastAndNextBookingDto(itemRepository.getById(id), lastBooking, nextBooking, commentsCreatedStringDto);
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

    @Transactional()
    @Override
    public List<ItemAndLastAndNextBookingDto> getAllItemtoUser(Long owner) {
        validationIdOwner(owner);
        List<ItemAndLastAndNextBookingDto> listBookings = new ArrayList<>();
        List<CommentCreatedStringDto> commentsCreatedStringDto = new ArrayList<>();

        List<Item> items = itemRepository.findItemsByOwner(owner);

        for (Item item : items) {
            LocalDateTime time = LocalDateTime.now();
            List<Booking> bookingsEnd = bookingRepository.findBookingsByItemAndEndBeforeOrderByEndDesc(item, time);

            List<Status> status = Arrays.asList(Status.APPROVED, Status.CURRENT, Status.PAST);
            List<Booking> bookingsStart = bookingRepository.findBookingsByItemAndStatusInAndStartAfterOrderByStartAsc(item, status, time);

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


            for (Comment comment : commentRepository.findAllByItem(item)) {
                CommentCreatedStringDto commentCreatedStringDto = CommentMapper.toCommentCreatedStringDto(comment);

                commentCreatedStringDto.setAuthorName(comment.getAuthor().getName());
                commentsCreatedStringDto.add(commentCreatedStringDto);
            }

            listBookings.add(ItemMapper.toItemAndLastAndNextBookingDto(item, lastBooking, nextBooking, commentsCreatedStringDto));

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
        validationIdOwner(owner);
        validationIdItem(id);
        validationIdOwnerHaveBookingItem(owner, id);
        validationComment(commentDto);

        commentDto.setAuthor(userRepository.findUserById(owner));
        commentDto.setItem(itemRepository.findItemById(id));

        commentDto.setCreated(LocalDateTime.now());

        Comment comment = CommentMapper.toComment(commentDto);

        commentDto = CommentMapper.toCommentDto(commentRepository.save(comment));
        commentDto.setAuthorName(commentDto.getAuthor().getName());

        return commentDto;
    }
}