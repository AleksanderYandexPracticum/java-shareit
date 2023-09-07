package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class RequestRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RequestRepository requestRepository;

    private Long userId;
    private Long itemRequest1Id;
    private Long itemRequest2Id;
    private Long itemRequest3Id;


    @BeforeEach
    private void add() {
        User user1 = new User("Jon", "jon@mail.ru");
        this.userId = userRepository.save(user1).getId();

        ItemRequest itemRequest1 = ItemRequest.builder()
                .description("тамагавк нужен")
                .requestor(userId)
                .created(LocalDateTime.now())
                .build();
        ItemRequest itemRequest2 = ItemRequest.builder()
                .description("тамагавк нужен")
                .requestor(userId)
                .created(LocalDateTime.now().plusDays(1L))
                .build();
        ItemRequest itemRequest3 = ItemRequest.builder()
                .description("тамагавк нужен")
                .requestor(userId)
                .created(LocalDateTime.now().plusDays(2L))
                .build();
        this.itemRequest1Id = requestRepository.save(itemRequest1).getId();
        this.itemRequest2Id = requestRepository.save(itemRequest2).getId();
        this.itemRequest3Id = requestRepository.save(itemRequest3).getId();
    }

    @AfterEach
    private void deleteUsers() {
        requestRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    void getItemRequestByRequestorOrderByCreatedDesc() {
        Long owner = userId;
        List<ItemRequest> list = requestRepository.getItemRequestByRequestorOrderByCreatedDesc(owner);
        assertTrue(list.size() == 3);
        assertEquals(itemRequest3Id, list.get(0).getId());
        assertEquals(itemRequest2Id, list.get(1).getId());
        assertEquals(itemRequest1Id, list.get(2).getId());
    }

    @Test
    void getItemRequestByRequestorNotOrderByCreatedDesc() {
        Long owner = 0L;
        Integer from = 1;
        Integer size = 2;
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        Integer pageNumber = from / size;
        Pageable pageable = PageRequest.of(pageNumber, size, sort);
        List<ItemRequest> list = requestRepository.getItemRequestByRequestorNotOrderByCreatedDesc(owner, pageable);

        assertTrue(list.size() == 2);
        assertEquals(itemRequest3Id, list.get(0).getId());
        assertEquals(itemRequest2Id, list.get(1).getId());
    }

    @Test
    void testGetItemRequestByRequestorNotOrderByCreatedDesc() {
        Long owner = 0L;
        List<ItemRequest> list = requestRepository.getItemRequestByRequestorNotOrderByCreatedDesc(owner);

        assertTrue(list.size() == 3);
        assertEquals(itemRequest3Id, list.get(0).getId());
        assertEquals(itemRequest2Id, list.get(1).getId());
        assertEquals(itemRequest1Id, list.get(2).getId());
    }

    @Test
    void getItemRequestByIdOrderByCreatedDesc() {
        Long requestId = itemRequest1Id;
        List<ItemRequest> list = requestRepository.getItemRequestByIdOrderByCreatedDesc(requestId);

        assertEquals(itemRequest1Id, list.get(0).getId());
    }
}