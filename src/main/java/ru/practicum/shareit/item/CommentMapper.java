package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentCreatedStringDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {

//        LocalDateTime created = LocalDateTime.parse(comment.getCreated());

        return new CommentDto(comment.getId(),
                comment.getText(),
                comment.getItem(),
                comment.getAuthor(),
                comment.getCreated());
    }

    public static CommentCreatedStringDto toCommentCreatedStringDto(Comment comment) {

        String createdDate = comment.getCreated()
                .atOffset(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy.MM.dd hh:mm:ss")); // "2023-08-20T12:59:10



        return new CommentCreatedStringDto(comment.getId(),
                comment.getText(),
                comment.getItem(),
                comment.getAuthor(),
                createdDate);
    }


    public static Comment toComment(CommentDto commentDto) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        return new Comment(commentDto.getText(),
                commentDto.getItem(),
                commentDto.getAuthor(),
                commentDto.getCreated());
    }
}
