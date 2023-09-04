package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemAndLastAndNextBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemServiceImpl;

    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;

    @Captor
    private ArgumentCaptor<Comment> commentArgumentCaptor;


    @Test
    void add() {
        Long owner = 1L;
        ItemDto itemDto = ItemDto.builder()
                .id(null)
                .name("молоток")
                .description("маленький")
                .available(true)
                .requestId(null)
                .build();

        Item item = Item.builder()
                .id(null)
                .name("молоток")
                .description("маленький")
                .available(true)
                .owner(owner)
                .requestId(null)
                .build();

        when(itemRepository.save(item)).thenReturn(item);
        when(userRepository.existsById(anyLong())).thenReturn(true);

        ItemDto actualItemDto = itemServiceImpl.add(owner, itemDto);

        Assertions.assertEquals(item.getName(), actualItemDto.getName());
        Assertions.assertEquals(item.getDescription(), actualItemDto.getDescription());
        Assertions.assertEquals(ItemMapper.toItemDto(item), actualItemDto);

        ItemDto itemDto1 = ItemDto.builder()
                .id(null)
                .name(null)
                .description("маленький")
                .available(true)
                .requestId(null)
                .build();

        Assertions.assertThrows(ValidationException.class, () -> itemServiceImpl.add(owner, itemDto1));

        ItemDto itemDto2 = ItemDto.builder()
                .id(null)
                .name("молоток")
                .description(null)
                .available(true)
                .requestId(null)
                .build();
        Assertions.assertThrows(ValidationException.class, () -> itemServiceImpl.add(owner, itemDto2));

        ItemDto itemDto3 = ItemDto.builder()
                .id(null)
                .name("молоток")
                .description("маленький")
                .available(null)
                .requestId(null)
                .build();
        Assertions.assertThrows(ValidationException.class, () -> itemServiceImpl.add(owner, itemDto3));

        when(userRepository.existsById(anyLong())).thenReturn(false);
        Assertions.assertThrows(NotFoundException.class, () -> itemServiceImpl.add(owner, itemDto));
    }

    @Test
    void get() {
        Long id = 2L;
        Long owner = 1L;
        Item item = Item.builder()
                .id(id)
                .name("молоток")
                .description("маленький")
                .available(true)
                .owner(owner)
                .requestId(null)
                .build();
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.getItemByIdAndOwner(id, owner)).thenReturn(null);


        when(itemRepository.findItemById(anyLong())).thenReturn(item);
        List<Comment> comment = List.of();
        when(commentRepository.getCommentsByItem(item)).thenReturn(comment);
        when(itemRepository.getById(anyLong())).thenReturn(item);


        ItemAndLastAndNextBookingDto itemAndLastAndNextBookingDto = itemServiceImpl.get(id, owner);


        Assertions.assertEquals(item.getName(), itemAndLastAndNextBookingDto.getName());
        Assertions.assertEquals(item.getDescription(), itemAndLastAndNextBookingDto.getDescription());

        when(itemRepository.existsById(anyLong())).thenReturn(false);
        Assertions.assertThrows(NotFoundException.class, () -> itemServiceImpl.get(id, owner));
    }

    @Test
    void update() {
        Long id = 2L;
        Long owner = 1L;

        ItemDto itemDto = ItemDto.builder()
                .id(null)
                .name("молоток_NEW")
                .description("маленький_NEW")
                .available(true)
                .requestId(null)
                .build();

        Item item = Item.builder()
                .id(id)
                .name("молоток")
                .description("маленький")
                .available(true)
                .owner(owner)
                .requestId(null)
                .build();
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findItemById(anyLong())).thenReturn(item);

        when(itemRepository.getById(anyLong())).thenReturn(item);

        when(itemRepository.save(any())).thenReturn(item);

        itemServiceImpl.update(id, owner, itemDto);

        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item saveItem = itemArgumentCaptor.getValue();

        Assertions.assertEquals(itemDto.getName(), saveItem.getName());
        Assertions.assertEquals(itemDto.getDescription(), saveItem.getDescription());

        when(itemRepository.save(any())).thenReturn(saveItem);
        ItemDto actualItemDto = itemServiceImpl.update(id, owner, itemDto);

        Assertions.assertEquals(saveItem.getName(), actualItemDto.getName());
        Assertions.assertEquals(saveItem.getDescription(), actualItemDto.getDescription());
        Assertions.assertEquals(ItemMapper.toItemDto(saveItem), actualItemDto);

        item.setOwner(2L);
        when(itemRepository.findItemById(anyLong())).thenReturn(item);
        Assertions.assertThrows(NotFoundException.class, () -> itemServiceImpl.update(id, owner, itemDto));

    }

    @Test
    void getAllItemToUser() {
        Long id = 2L;
        Long owner = 1L;
        int from = 0;
        int size = 1;

        Item item = Item.builder()
                .id(id)
                .name("молоток")
                .description("маленький")
                .available(true)
                .owner(owner)
                .requestId(null)
                .build();
        List<Item> items = List.of(item);
        when(userRepository.existsById(anyLong())).thenReturn(true);

        Integer pageNumber = from / size;
        Pageable pageable = PageRequest.of(pageNumber, size);
        when(itemRepository.findItemsByOwner(owner, pageable)).thenReturn(items);

        List<Booking> bookingsEnd = new ArrayList<>();
        List<Booking> bookingsStart = new ArrayList<>();

        when(bookingRepository.findBookingsByItemAndEndBeforeOrderByEndDesc(any(), any(LocalDateTime.class))).thenReturn(bookingsEnd);
        when(bookingRepository.findBookingsByItemAndStatusInAndStartAfterOrderByStartAsc(any(), any(), any(LocalDateTime.class))).thenReturn(bookingsStart);


        User user = new User();
        user.setId(1L);
        List<Comment> comment = List.of(new Comment(1L, "", item, user, LocalDateTime.now()));
        when(commentRepository.findAllByItem(item)).thenReturn(comment);


        List<ItemAndLastAndNextBookingDto> itemsAndLastAndNextBookingDto = itemServiceImpl.getAllItemToUser(owner, from, size);


        Assertions.assertEquals(item.getName(), itemsAndLastAndNextBookingDto.get(0).getName());
        Assertions.assertEquals(item.getDescription(), itemsAndLastAndNextBookingDto.get(0).getDescription());
        Assertions.assertEquals(item.getAvailable(), itemsAndLastAndNextBookingDto.get(0).getAvailable());

        when(itemRepository.existsById(anyLong())).thenReturn(false);
        Assertions.assertThrows(NotFoundException.class, () -> itemServiceImpl.get(id, owner));

    }

    @Test
    void getAllItemWithText() {
        Long id = 2L;
        Long owner = 1L;
        Integer from = 0;
        Integer size = 1;

        Item item = Item.builder()
                .id(id)
                .name("молоток")
                .description("маленький")
                .available(true)
                .owner(owner)
                .requestId(null)
                .build();
        List<Item> items = List.of(item);
        when(userRepository.existsById(anyLong())).thenReturn(true);

        String text1 = "молот";
        String text2 = "молот";
        Integer pageNumber = from / size;
        Pageable pageable = PageRequest.of(pageNumber, size);

        when(itemRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(text1, text2, pageable)).thenReturn(items);

        List<ItemDto> itemsDto = itemServiceImpl.getAllItemWithText(text1, owner, from, size);

        Assertions.assertEquals(item.getName(), itemsDto.get(0).getName());
        Assertions.assertEquals(item.getDescription(), itemsDto.get(0).getDescription());
        Assertions.assertEquals(ItemMapper.toItemDto(item), itemsDto.get(0));

        text1 = "";
        itemsDto = itemServiceImpl.getAllItemWithText(text1, owner, from, size);
        Assertions.assertEquals(0, itemsDto.size());
    }

    @Test
    void addComment() {
        Long owner = 1L;
        Long id = 1L;
        Comment comment = Comment.builder()
                .text("Комментарий")
                .item(new Item())
                .author(new User())
                .created(LocalDateTime.now())
                .build();

        Item item = Item.builder()
                .id(id)
                .name("молоток")
                .description("маленький")
                .available(true)
                .owner(owner)
                .requestId(null)
                .build();

        CommentDto commentDto = CommentDto.builder()
                .text("Комментарий_NEW")
                .item(new Item())
                .author(new User())
                .created(LocalDateTime.now())
                .build();
        User user = new User("borya", "borya@mail.ru");
        List<Booking> list = List.of(new Booking(), new Booking());

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllBookingByItemIdAndBookerIdAndStatusNotIn(anyLong(), anyLong(), any())).thenReturn(list);
        when(bookingRepository.findBookingByItemIdAndBookerIdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class))).thenReturn(list);

        when(userRepository.findUserById(owner)).thenReturn(user);
        when(itemRepository.findItemById(id)).thenReturn(item);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto actualCommentDto = itemServiceImpl.addComment(owner, id, commentDto);

        verify(commentRepository).save(commentArgumentCaptor.capture());
        Comment saveComment = commentArgumentCaptor.getValue();


        Assertions.assertEquals(saveComment.getText(), commentDto.getText());
        Assertions.assertEquals(saveComment.getItem(), item);
        Assertions.assertEquals(saveComment.getAuthor(), user);

        Assertions.assertEquals(comment.getText(), actualCommentDto.getText());

        commentDto.setText("");
        Assertions.assertThrows(ValidationException.class, () -> itemServiceImpl.addComment(owner, id, commentDto));
    }
}