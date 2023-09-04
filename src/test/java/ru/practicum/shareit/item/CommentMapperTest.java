package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentCreatedStringDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommentMapperTest {
    LocalDateTime created = LocalDateTime.now();

    @Test
    void toCommentDto() {
        User user = new User("Jon", "jon@mail.ru");
        Comment comment = Comment.builder()
                .id(1L)
                .text("Комментарий")
                .item(new Item())
                .author(user)
                .created(created)
                .build();
        CommentDto commentDto = CommentMapper.toCommentDto(comment);
        assertEquals(commentDto.getText(), comment.getText());
        assertEquals(commentDto.getAuthor(), comment.getAuthor());
        assertTrue(commentDto.getCreated().equals(created));
    }

    @Test
    void toCommentCreatedStringDto() {
        User user = new User("Jon", "jon@mail.ru");
        Comment comment = Comment.builder()
                .id(1L)
                .text("Комментарий")
                .item(new Item())
                .author(user)
                .created(created)
                .build();
        CommentCreatedStringDto commentCreatedStringDto = CommentMapper.toCommentCreatedStringDto(comment);
        assertEquals(commentCreatedStringDto.getText(), comment.getText());
        assertEquals(commentCreatedStringDto.getAuthor(), comment.getAuthor());
        assertTrue(commentCreatedStringDto.getCreated().equals(created));
    }

    @Test
    void toComment() {
        CommentDto commentDto = CommentDto.builder()
                .text("Комментарий_NEW")
                .item(new Item())
                .author(new User("Jon", "jon@mail.ru"))
                .created(created)
                .build();
        Comment comment = CommentMapper.toComment(commentDto);
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(comment.getAuthor(), commentDto.getAuthor());
        assertTrue(commentDto.getCreated().equals(created));
    }
}