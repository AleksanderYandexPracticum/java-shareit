package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class CommentRepositoryIntegrationTest {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private Long user1Id;
    private Long user2Id;

    private User returnUser1;
    private User returnUser2;
    private Item returnItem1;
    private Item returnItem2;
    private Comment returnComment1;
    private Comment returnComment2;


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


        Comment comment1 = Comment.builder()
                .text("1-ый комментарий от 1 пользователя")
                .item(returnItem1)
                .author(returnUser1)
                .created(LocalDateTime.now())
                .build();
        Comment comment2 = Comment.builder()
                .text("2-ой комментарий от 1 пользователя")
                .item(returnItem1)
                .author(returnUser1)
                .created(LocalDateTime.now())
                .build();

        this.returnComment1 = commentRepository.save(comment1);
        this.returnComment2 = commentRepository.save(comment2);
    }

    @AfterEach
    private void deleteUsers() {
        commentRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void save() {
        Comment comment3 = Comment.builder()
                .text("1-sй комментарий от 2!!!!!!! пользователя")
                .item(returnItem1)
                .author(returnUser2)
                .created(LocalDateTime.now())
                .build();
        Comment actualComment = commentRepository.save(comment3);

        assertEquals(actualComment, comment3);
    }

    @Test
    void findAllByItem() {

        List<Comment> list = commentRepository.findAllByItem(returnItem1);
        assertTrue(list.size() == 2);
        assertEquals(list.get(0), returnComment1);
        assertEquals(list.get(1), returnComment2);
    }

    @Test
    void getCommentsByItem() {

        List<Comment> list = commentRepository.getCommentsByItem(returnItem1);
        assertTrue(list.size() == 2);
        assertEquals(list.get(0), returnComment1);
        assertEquals(list.get(1), returnComment2);
    }
}
