package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.Service.RequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    @Mock
    private RequestService requestService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private ItemRequestController itemRequestController;

    @Test
    void add() {
        Long owner = 1L;
        Long id = 1L;
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(null)
                .description("Описание")
                .requestor(1L)
                .created(LocalDateTime.now())
                .items(new ArrayList<ItemDto>())
                .build();
        when(requestService.add(anyLong(), any(ItemRequestDto.class), any(LocalDateTime.class))).thenReturn(itemRequestDto);

        ItemRequestDto actualItemRequestDto = itemRequestController.add(request, owner, itemRequestDto);

        assertEquals(itemRequestDto, actualItemRequestDto);
    }

    @Test
    void getYourRequestsWithResponse() {
        Long owner = 1L;
        Long id = 1L;
        Optional<String> state = Optional.of("All");
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(id)
                .description("Описание")
                .requestor(1L)
                .created(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();
        List<ItemRequestDto> list = List.of(itemRequestDto);
        when(requestService.getYourRequestsWithResponse(owner)).thenReturn(list);

        List<ItemRequestDto> actualItemsRequestDto = itemRequestController.getYourRequestsWithResponse(request, owner, state);

        assertEquals(itemRequestDto.getDescription(), actualItemsRequestDto.get(0).getDescription());

    }

    @Test
    void listOfRequestsFromOtherUsers() {
        Long owner = 1L;
        Long id = 1L;
        Integer from = 0;
        Integer size = 1;
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(id)
                .description("Описание")
                .requestor(1L)
                .created(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();
        List<ItemRequestDto> list = List.of(itemRequestDto);
        when(requestService.listOfRequestsFromOtherUsers(owner, from, size)).thenReturn(list);

        List<ItemRequestDto> actualItemsRequestDto = itemRequestController.listOfRequestsFromOtherUsers(request, owner, from, size);

        assertEquals(itemRequestDto.getDescription(), actualItemsRequestDto.get(0).getDescription());

    }

    @Test
    void getRequestsWithResponse() {
        Long owner = 1L;
        Long id = 1L;
        Long requestId = 1L;

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(id)
                .description("Описание")
                .requestor(1L)
                .created(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

        when(requestService.getRequestsWithResponse(anyLong(), anyLong())).thenReturn(itemRequestDto);

        ItemRequestDto actualItemRequestDto = itemRequestController.getRequestsWithResponse(request, owner, requestId);

        assertEquals(itemRequestDto.getDescription(), actualItemRequestDto.getDescription());
    }
}