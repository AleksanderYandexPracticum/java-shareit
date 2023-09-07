package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.Service.RequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private static final String OWNER_ID_HEADER = "X-Sharer-User-Id";
    private final RequestService requestService;

    @Autowired
    public ItemRequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ItemRequestDto add(HttpServletRequest request,
                              @RequestHeader(OWNER_ID_HEADER) Long owner,
                              @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Request to the endpoint was received: '{} {}', string of request parameters: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return requestService.add(owner, itemRequestDto, LocalDateTime.now());
    }

    @GetMapping
    public List<ItemRequestDto> getYourRequestsWithResponse(HttpServletRequest request,
                                                            @RequestHeader(OWNER_ID_HEADER) Long owner,
                                                            @RequestParam(value = "state") Optional<String> state) {
        log.info("Request to the endpoint was received: '{} {}', string of request parameters: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return requestService.getYourRequestsWithResponse(owner);
    }


    @GetMapping("/all")
    public List<ItemRequestDto> listOfRequestsFromOtherUsers(HttpServletRequest request,
                                                             @RequestHeader(OWNER_ID_HEADER) Long owner,
                                                             @RequestParam(required = false, value = "from") Integer from,
                                                             @RequestParam(required = false, value = "size") Integer size) {
        log.info("Request to the endpoint was received: '{} {}', string of request parameters: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return requestService.listOfRequestsFromOtherUsers(owner, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestsWithResponse(HttpServletRequest request,
                                                  @RequestHeader(OWNER_ID_HEADER) Long owner,
                                                  @PathVariable("requestId") Long requestId) {
        log.info("Request to the endpoint was received: '{} {}', string of request parameters: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return requestService.getRequestsWithResponse(owner, requestId);
    }
}
