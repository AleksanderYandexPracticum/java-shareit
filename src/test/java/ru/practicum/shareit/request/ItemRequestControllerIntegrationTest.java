package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.Service.RequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;


import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestService requestService;


    @SneakyThrows
    @Test
    void add() {

        Long owner = 1L;
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(null)
                .description("тамагавк нужен")
                .requestor(1L)
                .created(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();
        when(requestService.add(any(), any(), any())).thenReturn(itemRequestDto);

        String result = mockMvc.perform(post("/requests")
                .header("X-Sharer-User-Id", 1L)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Assertions.assertEquals(objectMapper.writeValueAsString(itemRequestDto), result);
    }

    @SneakyThrows
    @Test
    void getYourRequestsWithResponse() {
        Long owner = 1L;
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(null)
                .description("тамагавк нужен")
                .requestor(1L)
                .created(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();
        List<ItemRequestDto> list = List.of(itemRequestDto);

        when(requestService.getYourRequestsWithResponse(owner)).thenReturn(list);

        mockMvc.perform(get("/requests")
                .header("X-Sharer-User-Id", 1L))
                .andDo(print())
                .andExpect(status().isOk());

        verify(requestService).getYourRequestsWithResponse(owner);

    }

    @SneakyThrows
    @Test
    void listOfRequestsFromOtherUsers() {
        Integer from = 0;
        Integer size = 1;
        Long owner = 1L;
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(null)
                .description("тамагавк нужен")
                .requestor(1L)
                .created(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();
        List<ItemRequestDto> list = List.of(itemRequestDto);

        when(requestService.listOfRequestsFromOtherUsers(owner, from, size)).thenReturn(list);

        mockMvc.perform(get("/requests/all")
                .param("from", "0")
                .param("size", "1")
                .header("X-Sharer-User-Id", 1L))
                .andDo(print())
                .andExpect(status().isOk());

        verify(requestService).listOfRequestsFromOtherUsers(owner, from, size);

    }

    @SneakyThrows
    @Test
    void getRequestsWithResponse() {
        Long owner = 1L;
        Long requestId = 1L;
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(null)
                .description("тамагавк нужен")
                .requestor(1L)
                .created(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

        when(requestService.getRequestsWithResponse(owner, requestId)).thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                .header("X-Sharer-User-Id", 1L))
                .andDo(print())
                .andExpect(status().isOk());

        verify(requestService).getRequestsWithResponse(owner, requestId);

    }
}