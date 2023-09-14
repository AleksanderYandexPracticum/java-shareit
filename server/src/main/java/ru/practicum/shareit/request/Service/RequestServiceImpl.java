package ru.practicum.shareit.request.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public RequestServiceImpl(RequestRepository requestRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }


    @Transactional
    @Override
    public ItemRequestDto add(Long owner, ItemRequestDto itemRequestDto, LocalDateTime createdTime) {
        validateUser(owner);
        ItemRequest itemRequest = RequestMapper.toItemRequest(owner, itemRequestDto, createdTime);

        return RequestMapper.toItemRequestDto(requestRepository.save(itemRequest));
    }


    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> getYourRequestsWithResponse(Long owner) {
        validateUser(owner);
        List<ItemRequest> listItemRequest = requestRepository.getItemRequestByRequestorOrderByCreatedDesc(owner);  // Получаю все запросы пользователя

        Map<Long, ItemRequestDto> mapItemRequestDto = getItemRequestDtoWithResponse(listItemRequest);

        return new ArrayList<>(mapItemRequestDto.values());
    }


    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> listOfRequestsFromOtherUsers(Long owner, Integer from, Integer size) {

        List<ItemRequest> page = null;
        if (from != null && size != null) {
            Sort sort = Sort.by(Sort.Direction.DESC, "created");
            Integer pageNumber = from / size;
            Pageable pageable = PageRequest.of(pageNumber, size, sort);
            page = requestRepository.getItemRequestByRequestorNotOrderByCreatedDesc(owner, pageable);
        } else {
            page = requestRepository.getItemRequestByRequestorNotOrderByCreatedDesc(owner);
        }

        Map<Long, ItemRequestDto> mapItemRequestDto = getItemRequestDtoWithResponse(page);

        return new ArrayList<>(mapItemRequestDto.values());
    }


    @Transactional(readOnly = true)
    @Override
    public ItemRequestDto getRequestsWithResponse(Long owner, Long requestId) {
        validateUser(owner);
        List<ItemRequest> listItemRequest = requestRepository.getItemRequestByIdOrderByCreatedDesc(requestId);

        if (listItemRequest.size() == 0) {
            log.info(String.format("There is no requestId № %s", requestId));
            throw new NotFoundException(String.format("There is no requestId № %s", requestId));
        }

        Map<Long, ItemRequestDto> mapItemRequestDto = getItemRequestDtoWithResponse(listItemRequest);

        return mapItemRequestDto.values().stream().findFirst().get();
    }


    private void validateUser(Long owner) {
        if (!userRepository.existsById(owner)) {
            log.info("There is no such owner ID");
            throw new NotFoundException(String.format("There is no such owner ID № %s", owner));
        }
    }

    private Map<Long, ItemRequestDto> getItemRequestDtoWithResponse(List<ItemRequest> itemRequests) {
        List<Long> requestIds = itemRequests.stream()
                .map((itemRequest) -> itemRequest.getId())
                .collect(Collectors.toList());

        List<Item> listItem = itemRepository.getItemsByRequestIdIn(requestIds);

        List<ItemDto> listItemDto = listItem.stream()
                .map(item -> ItemMapper.toItemDto(item))
                .collect(Collectors.toList());

        Map<Long, ItemRequest> mapItemRequest = itemRequests.stream()
                .collect(Collectors.toMap(ItemRequest::getId, itemRequest -> itemRequest));

        Map<Long, ItemRequestDto> mapItemRequestDto = mapItemRequest.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> RequestMapper.toItemRequestDto(entry.getValue())));

        for (Map.Entry<Long, ItemRequestDto> entry : mapItemRequestDto.entrySet()) {
            for (int i = 0; i < listItemDto.size(); i++) {
                if (listItemDto.get(i).getRequestId().equals(entry.getKey())) {
                    entry.getValue().getItems().add(listItemDto.get(i));
                    listItemDto.remove(listItemDto.get(i));
                }
            }
        }
        return mapItemRequestDto;
    }
}