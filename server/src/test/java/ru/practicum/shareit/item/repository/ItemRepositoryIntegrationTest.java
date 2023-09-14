package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ItemRepositoryIntegrationTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RequestRepository requestRepository;

    private Long user1Id;
    private Long user2Id;

    private User returnUser1;
    private User returnUser2;
    private Item returnItem1;
    private Item returnItem2;

    private Long itemRequest1Id;
    private Long itemRequest2Id;


    @BeforeEach
    private void addUser() {
        User user1 = new User("Jon", "jon@mail.ru");
        User user2 = new User("Piter", "pit@yandex.ru");
        this.returnUser1 = userRepository.save(user1);
        this.returnUser2 = userRepository.save(user2);

        this.user1Id = returnUser1.getId();
        this.user2Id = returnUser2.getId();

        Item item1 = Item.builder()
                .name("молоток")
                .description("маленький")
                .available(true)
                .owner(user1Id)
                .requestId(null)
                .build();
        Item item2 = Item.builder()
                .name("топор")
                .description("большой")
                .available(true)
                .owner(user2Id)
                .requestId(null)
                .build();
        this.returnItem1 = itemRepository.save(item1);
        this.returnItem2 = itemRepository.save(item2);

        ItemRequest itemRequest1 = ItemRequest.builder()
                .description("спининг нужен")
                .requestor(user1Id)
                .created(LocalDateTime.now())
                .build();
        ItemRequest itemRequest2 = ItemRequest.builder()
                .description("плита нужна")
                .requestor(user2Id)
                .created(LocalDateTime.now().plusDays(1L))
                .build();
        this.itemRequest1Id = requestRepository.save(itemRequest1).getId();
        this.itemRequest2Id = requestRepository.save(itemRequest2).getId();
    }

    @AfterEach
    private void deleteUsers() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void save() {
        Item item3 = Item.builder()
                .name("Пила")
                .description("цепная")
                .available(true)
                .owner(user1Id)
                .requestId(null)
                .build();
        Item actualItem = itemRepository.save(item3);
        assertEquals(actualItem, item3);
    }

    @Test
    void getById() {
        Item actualItem = itemRepository.getById(returnItem2.getId());
        assertEquals(actualItem, returnItem2);
    }

    @Test
    void findItemById() {
        Item actualItem = itemRepository.findItemById(returnItem1.getId());
        assertEquals(actualItem, returnItem1);
    }

    @Test
    void findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue() {
        String text1 = "топор";
        String text2 = text1;
        List<Item> list = itemRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(text1, text2);
        assertEquals(list.get(0), returnItem2);
        text1 = "дрель";
        text2 = "больш";
        list = itemRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(text1, text2);
        assertEquals(list.get(0), returnItem2);
        text1 = "дрель";
        text2 = "красная";
        list = itemRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(text1, text2);
        assertTrue(list.size() == 0);
    }

    @Test
    void findItemsByOwner() {
        List<Item> list = itemRepository.findItemsByOwnerOrderByIdAsc(user1Id);
        assertEquals(list.get(0), returnItem1);
    }

    @Test
    void getItemByIdAndOwner() {
        Item actualItem = itemRepository.getItemByIdAndOwner(returnItem1.getId(), user1Id);
        assertEquals(actualItem, returnItem1);
        Item actualItem1 = itemRepository.getItemByIdAndOwner(returnItem1.getId(), user2Id);
        assertTrue(actualItem1 == null);
    }

    @Test
    void getItemsByRequestIdIn() {
        Item item3 = Item.builder()
                .name("Удочка")
                .description("спининг")
                .available(true)
                .owner(user1Id)
                .requestId(itemRequest1Id)
                .build();
        Item item4 = Item.builder()
                .name("Плита")
                .description("газовая")
                .available(true)
                .owner(user1Id)
                .requestId(itemRequest2Id)
                .build();
        itemRepository.save(item3);
        itemRepository.save(item4);

        List<Long> requestIds = List.of(itemRequest1Id, itemRequest2Id);
        List<Item> list = itemRepository.getItemsByRequestIdIn(requestIds);
        assertTrue(list.size() == 2);
    }

    @Test
    void testFindAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue() {
        String text1 = "молоток";
        String text2 = text1;
        Integer from = 0;
        Integer size = 2;
        Integer pageNumber = from / size;
        Pageable pageable = PageRequest.of(pageNumber, size);
        List<Item> list = itemRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(text1, text2, pageable);
        assertTrue(list.size() == 1);
        assertEquals(list.get(0), returnItem1);
    }

    @Test
    void testFindItemsByOwner() {
        Integer from = 0;
        Integer size = 2;
        Integer pageNumber = from / size;
        Pageable pageable = PageRequest.of(pageNumber, size);
        List<Item> list = itemRepository.findItemsByOwnerOrderByIdAsc(user1Id, pageable);
        assertTrue(list.size() == 1);
        assertEquals(list.get(0), returnItem1);

        list = itemRepository.findItemsByOwnerOrderByIdAsc(0L, pageable);
        assertTrue(list.size() == 0);
    }
}