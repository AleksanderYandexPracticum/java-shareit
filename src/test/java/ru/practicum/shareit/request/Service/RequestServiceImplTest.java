package ru.practicum.shareit.request.Service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;


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
    }

    @Test
    void getYourRequestsWithResponse() {
    }

    @Test
    void listOfRequestsFromOtherUsers() {
    }

    @Test
    void getRequestsWithResponse() {
    }
}