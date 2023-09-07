package ru.practicum.shareit.request.Service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface RequestService {

    ItemRequestDto add(Long owner, ItemRequestDto itemRequestDto, LocalDateTime cratedTime);

    List<ItemRequestDto> getYourRequestsWithResponse(Long owner);

    List<ItemRequestDto> listOfRequestsFromOtherUsers(Long owner, Integer from, Integer size);

    ItemRequestDto getRequestsWithResponse(Long owner, Long requestId);
}
