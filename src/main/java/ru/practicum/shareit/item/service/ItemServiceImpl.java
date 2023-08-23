package ru.practicum.shareit.item.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingNewNameIdDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.QBooking;
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

    @Transactional
    @Override
    public ItemDto add(Long owner, ItemDto itemDto) {
        validateItem(itemDto);
        validateIdOwner(owner);
        Item item = ItemMapper.toItem(owner, itemDto);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional()
    @Override
    public ItemAndLastAndNextBookingDto get(Long id, Long owner) {
        validateIdOwner(owner);
        validateIdItem(id);

        LocalDateTime time = LocalDateTime.now();
        BookingNewNameIdDto lastBooking = null;
        BookingNewNameIdDto nextBooking = null;

        Item item = itemRepository.getItemByIdAndOwner(id, owner);
        if (item != null) {

            BooleanExpression byItem = QBooking.booking.item.eq(item);
            BooleanExpression startBefore = QBooking.booking.start.before(time);
            BooleanExpression endAfter = QBooking.booking.end.after(time);
            BooleanExpression endBefore = QBooking.booking.end.before(time);
            Sort sort = Sort.by(Sort.Direction.DESC, "end");

            List<Booking> bookingsEnd = (List<Booking>) bookingRepository.findAll(byItem.and(startBefore).and(endAfter).or(byItem.and(endBefore)), sort);

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
        validateIdOwner(owner);
        validateIdItem(id);
        validateIdItemAndIdOwner(id, owner);
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
        validateIdOwner(owner);
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
        validateIdOwner(owner);
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
        validateIdOwner(owner);
        validateIdItem(id);
        validateIdOwnerHaveBookingItem(owner, id);
        validateComment(commentDto);

        commentDto.setAuthor(userRepository.findUserById(owner));
        commentDto.setItem(itemRepository.findItemById(id));

        commentDto.setCreated(LocalDateTime.now());

        Comment comment = CommentMapper.toComment(commentDto);

        commentDto = CommentMapper.toCommentDto(commentRepository.save(comment));
        commentDto.setAuthorName(commentDto.getAuthor().getName());

        return commentDto;
    }

    private void validateItem(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            log.info("The name of the item cannot be empty");
            throw new ValidationException("The name of the item cannot be empty");
        } else if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            log.info("The description cannot be empty");
            throw new ValidationException("The description cannot be empty");
        } else if (itemDto.getAvailable() == null) {
            log.info("The rental status cannot be empty");
            throw new ValidationException("The rental status cannot be empty");
        }
    }

    private void validateIdOwner(Long owner) {
        if (!userRepository.existsById(owner)) {
            log.info("There is no such owner ID");
            throw new NotFoundException(String.format("There is no such owner ID № %s", owner));
        }
    }

    private void validateIdItem(Long id) {
        if (!itemRepository.existsById(id)) {
            log.info("There is no such identifier");
            throw new NotFoundException(String.format("There is no such identifier № %s", id));
        }
    }

    private void validateIdItemAndIdOwner(Long id, Long owner) {
        if (!itemRepository.findItemById(id).getOwner().equals(owner)) {
            log.info(String.format("Owner  № %s doesn't have an item with an ID  № %s", owner, id));
            throw new NotFoundException(String.format("Owner  № %s doesn't have an item with an ID  № %s", owner, id));
        }
    }

    private void validateIdOwnerHaveBookingItem(Long owner, Long id) {
        LocalDateTime time = LocalDateTime.now();
        List<Status> status = Arrays.asList(Status.REJECTED, Status.CANCELED, Status.WAITING, Status.FUTURE);
        if (bookingRepository.findAllBookingByItemIdAndBookerIdAndStatusNotIn(id, owner, status).size() == 0
                || bookingRepository.findBookingByItemIdAndBookerIdAndEndBefore(id, owner, time).size() == 0) {
            log.info("Only that user can leave a review, " +
                    "who rented this thing, and only after the end of the lease period");
            throw new ValidationException(String.format("Only that user can leave a review, " +
                    "who rented this thing, and only after the end of the lease period " +
                    "Owner  № %s  the thing with the ID  № %s", owner, id));
        }
    }

    private void validateComment(CommentDto commentDto) {
        if (commentDto.getText() == null || commentDto.getText().isBlank()) {
            log.info("The comment cannot be empty");
            throw new ValidationException("The comment cannot be empty");
        }
    }
}