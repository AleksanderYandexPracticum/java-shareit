package ru.practicum.shareit.request.Service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    @Mock
    private RequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    RequestServiceImpl requestServiceImpl;

    @Test
    void add() {
        Long owner = 1L;
        LocalDateTime createdTime = LocalDateTime.now();
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(null)
                .description("тамагавк нужен")
                .requestor(1L)
                .created(createdTime)
                .items(new ArrayList<>())
                .build();
        when(userRepository.existsById(any())).thenReturn(true);
        ItemRequest itemRequest = RequestMapper.toItemRequest(owner, itemRequestDto, createdTime);
        when(requestRepository.save(itemRequest)).thenReturn(itemRequest);

        ItemRequestDto actualItemRequestDto = requestServiceImpl.add(owner, itemRequestDto, createdTime);
        Mockito.verify(requestRepository, Mockito.times(1)).save(itemRequest);
        assertEquals(actualItemRequestDto, itemRequestDto);
    }

    @Test
    void getYourRequestsWithResponse() {
        Long owner = 1L;

        LocalDateTime createdTime = LocalDateTime.now();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(null)
                .description("тамагавк нужен")
                .requestor(1L)
                .created(createdTime)
                .build();
        List<ItemRequest> listItemRequest = List.of(itemRequest);

        when(userRepository.existsById(any())).thenReturn(true);
        when(requestRepository.getItemRequestByRequestorOrderByCreatedDesc(any())).thenReturn(listItemRequest);

        List<ItemRequestDto> actualItemsRequestDto = requestServiceImpl.getYourRequestsWithResponse(owner);

        assertEquals(itemRequest.getDescription(), actualItemsRequestDto.get(0).getDescription());
        assertEquals(itemRequest.getRequestor(), actualItemsRequestDto.get(0).getRequestor());

        when(userRepository.existsById(any())).thenReturn(false);
        assertThrows(NotFoundException.class, () -> requestServiceImpl.getYourRequestsWithResponse(owner));
    }

    @Test
    void listOfRequestsFromOtherUsers() {
        Integer from = 0;
        Integer size = 1;
        Long owner = 1L;
        LocalDateTime createdTime = LocalDateTime.now();
        ItemRequest itemRequest1 = ItemRequest.builder()
                .id(1L)
                .description("тамагавк нужен")
                .requestor(1L)
                .created(createdTime)
                .build();
        ItemRequest itemRequest2 = ItemRequest.builder()
                .id(2L)
                .description("Лопата нужна")
                .requestor(1L)
                .created(createdTime.plusDays(1L))
                .build();
        List<ItemRequest> listItemRequest = List.of(itemRequest1, itemRequest2);
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        Integer pageNumber = from / size;
        Pageable pageable = PageRequest.of(pageNumber, size, sort);
        when(requestRepository.getItemRequestByRequestorNotOrderByCreatedDesc(owner, pageable)).thenReturn(listItemRequest);


        List<ItemRequestDto> actualItemsRequestDto = requestServiceImpl.listOfRequestsFromOtherUsers(owner, from, size);

        assertEquals(itemRequest1.getDescription(), actualItemsRequestDto.get(0).getDescription());
        assertEquals(itemRequest1.getRequestor(), actualItemsRequestDto.get(0).getRequestor());
    }

    @Test
    void getRequestsWithResponse() {
        Long owner = 1L;
        Long requestId = 1L;

        LocalDateTime createdTime = LocalDateTime.now();
        ItemRequest itemRequest1 = ItemRequest.builder()
                .id(1L)
                .description("тамагавк нужен")
                .requestor(1L)
                .created(createdTime)
                .build();
        ItemRequest itemRequest2 = ItemRequest.builder()
                .id(2L)
                .description("Лопата нужна")
                .requestor(1L)
                .created(createdTime.plusDays(1L))
                .build();
        List<ItemRequest> listItemRequest = List.of(itemRequest1, itemRequest2);
        when(userRepository.existsById(any())).thenReturn(true);

        when(requestRepository.getItemRequestByIdOrderByCreatedDesc(requestId)).thenReturn(listItemRequest);

        ItemRequestDto actualItemsRequestDto = requestServiceImpl.getRequestsWithResponse(owner, requestId);

        assertEquals(itemRequest1.getDescription(), actualItemsRequestDto.getDescription());
        assertEquals(itemRequest1.getRequestor(), actualItemsRequestDto.getRequestor());

        when(userRepository.existsById(any())).thenReturn(false);
        assertThrows(NotFoundException.class, () -> requestServiceImpl.getRequestsWithResponse(owner, requestId));

        when(userRepository.existsById(any())).thenReturn(true);
        when(requestRepository.getItemRequestByIdOrderByCreatedDesc(requestId)).thenReturn(new ArrayList<>());
        assertThrows(NotFoundException.class, () -> requestServiceImpl.getRequestsWithResponse(owner, requestId));

    }
}