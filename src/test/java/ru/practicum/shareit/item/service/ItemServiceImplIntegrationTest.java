package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemAndLastAndNextBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
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
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceImplIntegrationTest {

    private final EntityManager em;
    private final ItemServiceImpl itemServiceImpl;
    private final UserServiceImpl userServiceImpl;
    private final BookingServiceImpl bookingServiceImpl;

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

        ItemDto itemDto1 = ItemDto.builder()
                .name("")
                .description("маленький")
                .available(true)
                .build();
        assertThrows(ValidationException.class, () -> itemServiceImpl.add(1L, itemDto1));

        ItemDto itemDto2 = ItemDto.builder()
                .name("Наковальня")
                .description("")
                .available(true)
                .build();
        assertThrows(ValidationException.class, () -> itemServiceImpl.add(1L, itemDto2));

        ItemDto itemDto3 = ItemDto.builder()
                .name("Наковальня")
                .description("тяжёлая")
                .available(null)
                .build();
        assertThrows(ValidationException.class, () -> itemServiceImpl.add(1L, itemDto3));

    }

    @Test
    void get() {
        UserDto userDto = new UserDto(null, "Jon", "jon@mail.ru");
        userServiceImpl.add(userDto);

        ItemDto itemDto = ItemDto.builder()
                .name("молоток")
                .description("маленький")
                .available(true)
                .build();
        itemServiceImpl.add(1L, itemDto);
        ItemAndLastAndNextBookingDto itemAndLastAndNextBookingDto = itemServiceImpl.get(1L, 1L);
        assertThat(itemAndLastAndNextBookingDto.getName(), equalTo(itemDto.getName()));

        assertThrows(NotFoundException.class, () -> itemServiceImpl.get(2L, 1L));
        assertThrows(NotFoundException.class, () -> itemServiceImpl.get(1L, 2L));

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
        assertThrows(ValidationException.class, () -> itemServiceImpl.getAllItemToUser(1L, -1, 2));
        assertThrows(ValidationException.class, () -> itemServiceImpl.getAllItemToUser(1L, 0, 0));

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
        assertThrows(ValidationException.class, () -> itemServiceImpl.getAllItemToUser(1L, -1, 2));
        assertThrows(ValidationException.class, () -> itemServiceImpl.getAllItemToUser(1L, 0, 0));

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
        user.setId(2L);

        ItemDto itemDto = ItemDto.builder()
                .name("молоток")
                .description("маленький")
                .available(true)
                .build();
        Item item = ItemMapper.toItem(1L, itemServiceImpl.add(1L, itemDto));
        item.setId(2L);

        RequestBookingDto requestBookingDto = new RequestBookingDto(
                LocalDateTime.now().plusDays(1L),
                LocalDateTime.now().plusDays(2L),
                1L);
        BookingDto actualBookingDto = bookingServiceImpl.add(2L, requestBookingDto, LocalDateTime.now());

        CommentDto commentDto = CommentDto.builder()
                .text("Комментарий")
                .item(item)
                .author(user)
                .created(LocalDateTime.now().plusDays(4L))
                .build();
//        TypedQuery<Booking> queryBooking = em.createQuery("update Booking b set b.status = :status where b.id=1", Booking.class);
//        Booking b = queryBooking.setParameter("status", "PAST").getSingleResult();

//        CommentDto returnCommentDto = itemServiceImpl.addComment(2L, 1L, commentDto);


////        TypedQuery<Comment> query = em.createQuery("Select c from Comment c where c.text = :text", Comment.class);
////        Comment comment = query.setParameter("text", returnCommentDto.getText()).getSingleResult();
////        assertThat(comment.getId(), notNullValue());
////        assertThat(comment.getText(), equalTo(returnCommentDto.getText()));
//
//        assertThrows(NotFoundException.class, () -> itemServiceImpl.addComment(2L, 1L, commentDto));
//        assertThrows(NotFoundException.class, () -> itemServiceImpl.addComment(1L, 2L, commentDto));
//
//        commentDto.setCreated(LocalDateTime.now().minusHours(1L));
//        assertThrows(ValidationException.class, () -> itemServiceImpl.addComment(1L, 1L, commentDto));
//
//        commentDto.setCreated(LocalDateTime.now().plusDays(4L));
//        commentDto.setText("");
//        assertThrows(ValidationException.class, () -> itemServiceImpl.addComment(1L, 1L, commentDto));

    }
}