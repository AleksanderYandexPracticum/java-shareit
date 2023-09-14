package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemAndLastAndNextBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;


import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceImplIntegrationTest {

    private final EntityManager em;
    private final ItemServiceImpl itemServiceImpl;
    private final UserServiceImpl userServiceImpl;
    private final BookingServiceImpl bookingServiceImpl;
    private final BookingRepository bookingRepository;

    @Test
    void add() {
        UserDto userDto = new UserDto(null, "Jon", "jon@mail.ru");
        userServiceImpl.add(userDto);

        ItemDto itemDto = ItemDto.builder()
                .name("молоток")
                .description("маленький")
                .available(true)
                .build();
        itemServiceImpl.add(1L, itemDto);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", itemDto.getName()).getSingleResult();
        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));

        assertThrows(NotFoundException.class, () -> itemServiceImpl.add(2L, itemDto));

        ItemAndLastAndNextBookingDto returnItemAndLastAndNextBookingDto = itemServiceImpl.get(1L, 1L);
        assertThat(returnItemAndLastAndNextBookingDto.getName(), equalTo(itemDto.getName()));

//        ItemDto itemDto1 = ItemDto.builder()
//                .name("")
//                .description("маленький")
//                .available(true)
//                .build();
//        assertThrows(ValidationException.class, () -> itemServiceImpl.add(1L, itemDto1));
//
//        ItemDto itemDto2 = ItemDto.builder()
//                .name("Наковальня")
//                .description("")
//                .available(true)
//                .build();
//        assertThrows(ValidationException.class, () -> itemServiceImpl.add(1L, itemDto2));
//
//        ItemDto itemDto3 = ItemDto.builder()
//                .name("Наковальня")
//                .description("тяжёлая")
//                .available(null)
//                .build();
//        assertThrows(ValidationException.class, () -> itemServiceImpl.add(1L, itemDto3));

    }

    @Test
    void get() {
        UserDto userDto = new UserDto(null, "Jon", "jon@mail.ru");
        userServiceImpl.add(userDto);

        UserDto userDto1 = new UserDto(null, "PegonJon", "pegonjon@mail.ru");
        UserDto returnUserDto1 = userServiceImpl.add(userDto1);

        ItemDto itemDto = ItemDto.builder()
                .name("молоток")
                .description("маленький")
                .available(true)
                .build();
        itemServiceImpl.add(1L, itemDto);
        ItemAndLastAndNextBookingDto itemAndLastAndNextBookingDto = itemServiceImpl.get(1L, 1L);
        assertThat(itemAndLastAndNextBookingDto.getName(), equalTo(itemDto.getName()));

        assertThrows(NotFoundException.class, () -> itemServiceImpl.get(2L, 1L));
        assertThrows(NotFoundException.class, () -> itemServiceImpl.get(1L, 3L));

        ItemDto itemDto1 = ItemDto.builder()
                .name("Кувалда")
                .description("Большая")
                .available(false)
                .build();
        itemServiceImpl.add(1L, itemDto1);


        LocalDateTime start = LocalDateTime.now().plusDays(1L);
        LocalDateTime end = LocalDateTime.now().plusDays(2L);

        RequestBookingDto requestBookingDto = new RequestBookingDto(start, end, 1L);

        BookingDto bookingDto1 = bookingServiceImpl.add(2L, requestBookingDto, LocalDateTime.now());
        BookingDto bookingDtoUpdate1 = bookingServiceImpl.update(1L, 1L, true);
        ItemAndLastAndNextBookingDto itemAndLastAndNextBookingDto1 = itemServiceImpl.get(1L, 1L);

        assertThat(itemAndLastAndNextBookingDto1.getName(), equalTo(itemDto.getName()));
        assertThat(itemAndLastAndNextBookingDto1.getDescription(), equalTo(itemDto.getDescription()));

        UserDto userDto2 = new UserDto(null, "ANDRE", "ANTOXA@mail.ru");
        UserDto returnUserDto2 = userServiceImpl.add(userDto2);

        requestBookingDto = new RequestBookingDto(LocalDateTime.now().plusDays(3L), LocalDateTime.now().plusDays(4L), 1L);
        BookingDto bookingDto2 = bookingServiceImpl.add(3L, requestBookingDto, LocalDateTime.now());
        BookingDto bookingDtoUpdate2 = bookingServiceImpl.update(1L, 2L, true);

        ItemAndLastAndNextBookingDto itemAndLastAndNextBookingDto2 = itemServiceImpl.get(1L, 1L);

        assertThat(itemAndLastAndNextBookingDto2.getNextBooking().getId(), equalTo(1L));
        assertTrue(itemAndLastAndNextBookingDto2.getLastBooking() == null);
    }

    @Test
    void update() {

    }

    @Test
    void getAllItemToUser() {
        UserDto userDto = new UserDto(null, "Jon", "jon@mail.ru");
        userServiceImpl.add(userDto);

        ItemDto itemDto = ItemDto.builder()
                .name("молоток")
                .description("маленький")
                .available(true)
                .build();
        itemServiceImpl.add(1L, itemDto);
        ItemDto itemDto1 = ItemDto.builder()
                .name("кувалда")
                .description("огромная")
                .available(true)
                .build();
        itemServiceImpl.add(1L, itemDto1);
        List<ItemAndLastAndNextBookingDto> list = itemServiceImpl.getAllItemToUser(1L, 0, 2);
        assertTrue(list.size() == 2);
        assertThrows(NotFoundException.class, () -> itemServiceImpl.getAllItemToUser(2L, 0, 2));
//        assertThrows(ValidationException.class, () -> itemServiceImpl.getAllItemToUser(1L, -1, 2));
//        assertThrows(ValidationException.class, () -> itemServiceImpl.getAllItemToUser(1L, 0, 0));

        list = itemServiceImpl.getAllItemToUser(1L, null, null);
        assertTrue(list.size() == 2);
    }

    @Test
    void getAllItemWithText() {
        UserDto userDto = new UserDto(null, "Jon", "jon@mail.ru");
        userServiceImpl.add(userDto);

        ItemDto itemDto = ItemDto.builder()
                .name("молоток")
                .description("маленький")
                .available(true)
                .build();
        itemServiceImpl.add(1L, itemDto);
        ItemDto itemDto1 = ItemDto.builder()
                .name("кувалда")
                .description("огромная")
                .available(true)
                .build();
        itemServiceImpl.add(1L, itemDto1);
        List<ItemDto> list = itemServiceImpl.getAllItemWithText("молот", 1L, 0, 2);
        assertTrue(list.size() == 1);
        assertThrows(NotFoundException.class, () -> itemServiceImpl.getAllItemToUser(2L, 0, 2));
//        assertThrows(ValidationException.class, () -> itemServiceImpl.getAllItemToUser(1L, -1, 2));
//        assertThrows(ValidationException.class, () -> itemServiceImpl.getAllItemToUser(1L, 0, 0));

        list = itemServiceImpl.getAllItemWithText("молот", 1L, null, null);
        assertTrue(list.size() == 1);

    }

    @Test
    void addComment() {
        UserDto userDto = new UserDto(null, "Jon", "jon@mail.ru");
        UserDto returnUserDto = userServiceImpl.add(userDto);
        User user = UserMapper.toUser(returnUserDto);
        user.setId(1L);

        UserDto userDto1 = new UserDto(null, "Pegon", "PegonJon@mail.ru");
        UserDto returnUserDto1 = userServiceImpl.add(userDto1);
        User user1 = UserMapper.toUser(returnUserDto1);
        user1.setId(2L);

        ItemDto itemDto = ItemDto.builder()
                .name("молоток")
                .description("маленький")
                .available(true)
                .build();
        Item item = ItemMapper.toItem(1L, itemServiceImpl.add(1L, itemDto));
        item.setId(1L);

        RequestBookingDto requestBookingDto = new RequestBookingDto(
                LocalDateTime.now().plusDays(1L),
                LocalDateTime.now().plusDays(2L),
                1L);
        BookingDto actualBookingDto = bookingServiceImpl.add(2L, requestBookingDto, LocalDateTime.now());

        Booking booking = BookingMapper.toBooking(item, user1, requestBookingDto, Status.APPROVED);
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().minusDays(2L));
        booking.setEnd(LocalDateTime.now().minusDays(1L));

        Booking booking1 = bookingRepository.save(booking);
        CommentDto commentDto = CommentDto.builder()
                .text("Комментарий")
                .item(item)
                .author(user1)
                .created(LocalDateTime.now().plusDays(4L))
                .build();
        CommentDto commentDto1 = CommentDto.builder()
                .text("Другой комментарий")
                .item(item)
                .author(user1)
                .created(LocalDateTime.now().plusDays(5L))
                .build();

        CommentDto returnCommentDto = itemServiceImpl.addComment(2L, 1L, commentDto);
        bookingRepository.save(booking);
        CommentDto returnCommentDto1 = itemServiceImpl.addComment(2L, 1L, commentDto1);
        bookingRepository.save(booking);

        TypedQuery<Comment> query = em.createQuery("Select c from Comment c where c.text = :text", Comment.class);
        Comment comment = query.setParameter("text", returnCommentDto.getText()).getSingleResult();
        assertThat(comment.getId(), notNullValue());
        assertThat(comment.getText(), equalTo(returnCommentDto.getText()));

        TypedQuery<Comment> queryList = em.createQuery("Select c from Comment c where c.item = :item", Comment.class);

        List<Comment> listComments = queryList.setParameter("item", item).getResultList();

        assertTrue(listComments.size() == 2);

        assertThrows(NotFoundException.class, () -> itemServiceImpl.addComment(3L, 1L, commentDto));
        assertThrows(NotFoundException.class, () -> itemServiceImpl.addComment(1L, 3L, commentDto));

        commentDto.setCreated(LocalDateTime.now().minusHours(1L));
        assertThrows(ValidationException.class, () -> itemServiceImpl.addComment(1L, 1L, commentDto));

        commentDto.setCreated(LocalDateTime.now().plusDays(7L));
        commentDto.setText("");
        assertThrows(ValidationException.class, () -> itemServiceImpl.addComment(1L, 1L, commentDto));

    }
}