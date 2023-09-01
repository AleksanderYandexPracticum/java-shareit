package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemAndLastAndNextBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import javax.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private ItemController itemController;

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
        when(itemService.add(owner, itemDto)).thenReturn(itemDto);

        ItemDto actualItemDto = itemController.add(request, owner, itemDto);

        assertEquals(itemDto, actualItemDto);
    }

    @Test
    void update() {
        Long owner = 1L;
        Long id = 1L;
        ItemDto itemDto = ItemDto.builder()
                .id(null)
                .name("молоток")
                .description("маленький")
                .available(true)
                .requestId(null)
                .build();
        when(itemService.update(id, owner, itemDto)).thenReturn(itemDto);

        ItemDto actualItemDto = itemController.update(request, owner, id, itemDto);

        assertEquals(itemDto, actualItemDto);
    }

    @Test
    void getUser() {
        Long owner = 1L;
        Long id = 1L;
        ItemDto itemDto = ItemDto.builder()
                .id(null)
                .name("молоток")
                .description("маленький")
                .available(true)
                .requestId(null)
                .build();

        Item item = ItemMapper.toItem(owner, itemDto);
        when(itemService.get(id, owner)).thenReturn(ItemMapper.toItemAndLastAndNextBookingDto(item, null, null, null));

        ItemAndLastAndNextBookingDto actualItem = itemController.getUser(request, owner, id);

        assertEquals(itemDto.getName(), actualItem.getName());
    }

    @Test
    void getAllItemtoUser() {
        Integer from = 0;
        Integer size = 1;
        Long owner = 1L;

        ItemDto itemDto = ItemDto.builder()
                .id(null)
                .name("молоток")
                .description("маленький")
                .available(true)
                .requestId(null)
                .build();
        Item item = ItemMapper.toItem(owner, itemDto);
        List<ItemAndLastAndNextBookingDto> list = List.of(ItemMapper.toItemAndLastAndNextBookingDto(item, null, null, null));
        when(itemService.getAllItemToUser(owner, from, size)).thenReturn(list);

        List<ItemAndLastAndNextBookingDto> actualItemsDto = itemController.getAllItemtoUser(request, owner, from, size);

        assertEquals(itemDto.getName(), actualItemsDto.get(0).getName());
    }

    @Test
    void getAllItemWithText() {
        Integer from = 0;
        Integer size = 1;
        String text = "мол";
        Long owner = 1L;

        ItemDto itemDto = ItemDto.builder()
                .id(null)
                .name("молоток")
                .description("маленький")
                .available(true)
                .requestId(null)
                .build();
        List<ItemDto> list = List.of(itemDto);
        when(itemService.getAllItemWithText(text, owner, from, size)).thenReturn(list);

        List<ItemDto> actualItemsDto = itemController.getAllItemWithText(request, owner, text, from, size);

        assertEquals(itemDto, actualItemsDto.get(0));
    }

    @Test
    void testAdd() {
        Long owner = 1L;
        Long id = 1L;

        CommentDto commentDto = CommentDto.builder()
                .id(null)
                .text("owner")
                .item(new Item())
                .author(new User())
                .created(LocalDateTime.now())
                .build();

        when(itemService.addComment(owner, id, commentDto)).thenReturn(commentDto);

        CommentDto actualCommentDto = itemController.add(request, owner, id, commentDto);

        assertEquals(commentDto, actualCommentDto);
    }
}