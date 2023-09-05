package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemAndLastAndNextBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;


import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerIntegrationTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;


    @SneakyThrows
    @Test
    void add() {
        Long owner = 1L;
        ItemDto itemDto = ItemDto.builder()
                .id(null)
                .name("тамагавк")
                .description("тупой")
                .available(true)
                .build();
        when(itemService.add(owner, itemDto)).thenReturn(itemDto);

        String result = mockMvc.perform(post("/items")
                .header("X-Sharer-User-Id", 1L)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Assertions.assertEquals(objectMapper.writeValueAsString(itemDto), result);

    }

    @SneakyThrows
    @Test
    void update() {
        Long id = 1L;
        Long owner = 1L;
        ItemDto itemDto = ItemDto.builder()
                .id(null)
                .name("тамагавк")
                .description("тупой")
                .available(true)
                .build();
        when(itemService.update(id, owner, itemDto)).thenReturn(itemDto);

        String result = mockMvc.perform(patch("/items/{itemId}", id)
                .header("X-Sharer-User-Id", 1L)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Assertions.assertEquals(objectMapper.writeValueAsString(itemDto), result);
    }

    @SneakyThrows
    @Test
    void getUser() {
        Long id = 1L;
        Long owner = 1L;
        ItemAndLastAndNextBookingDto item = ItemAndLastAndNextBookingDto.builder()
                .id(null)
                .name("тамагавк")
                .description("тупой")
                .available(true)
                .build();
        when(itemService.get(id, owner)).thenReturn(item);

        mockMvc.perform(get("/items/{itemId}", id)
                .header("X-Sharer-User-Id", 1L))
                .andDo(print())
                .andExpect(status().isOk());

        verify(itemService).get(id, owner);

    }

    @SneakyThrows
    @Test
    void getAllItemtoUser() {
        Long owner = 1L;
        Integer from = 0;
        Integer size = 1;

        ItemAndLastAndNextBookingDto item = ItemAndLastAndNextBookingDto.builder()
                .id(null)
                .name("тамагавк")
                .description("тупой")
                .available(true)
                .build();
        List<ItemAndLastAndNextBookingDto> list = List.of(item);
        when(itemService.getAllItemToUser(owner, from, size)).thenReturn(list);

        mockMvc.perform(get("/items")
                .param("from", "0")
                .param("size", "1")
                .header("X-Sharer-User-Id", 1L))
                .andDo(print())
                .andExpect(status().isOk());

        verify(itemService).getAllItemToUser(owner, from, size);

    }

    @SneakyThrows
    @Test
    void getAllItemWithText() {
        Long owner = 1L;
        Integer from = 0;
        Integer size = 1;
        String text = "там";

        ItemDto item = ItemDto.builder()
                .id(null)
                .name("тамагавк")
                .description("тупой")
                .available(true)
                .build();
        List<ItemDto> list = List.of(item);

        when(itemService.getAllItemWithText(text, owner, from, size)).thenReturn(list);
        mockMvc.perform(get("/items/search")
                .param("text", "там")
                .param("from", "0")
                .param("size", "1")
                .header("X-Sharer-User-Id", 1L))
                .andDo(print())
                .andExpect(status().isOk());

        verify(itemService).getAllItemWithText(text, owner, from, size);

    }

    @SneakyThrows
    @Test
    void testAdd() {
        Long owner = 1L;
        Long id = 1L;

        CommentDto commentDto = CommentDto.builder()
                .id(null)
                .text("тамагавк гудит")
                .item(new Item())
                .author(new User())
                .build();

        when(itemService.addComment(owner, id, commentDto)).thenReturn(commentDto);

        String result = mockMvc.perform(post("/items/{itemId}/comment", id)
                .header("X-Sharer-User-Id", 1L)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Assertions.assertEquals(objectMapper.writeValueAsString(commentDto), result);

    }
}