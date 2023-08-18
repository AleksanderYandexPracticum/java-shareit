package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentCreatedStringDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(),
                comment.getText(),
                comment.getItem(),
                comment.getAuthor(),
                comment.getCreated());
    }

    public static CommentCreatedStringDto toCommentCreatedStringDto(Comment comment) {
        return new CommentCreatedStringDto(comment.getId(),
                comment.getText(),
                comment.getItem(),
                comment.getAuthor(),
                comment.getCreated());
    }


    public static Comment toComment(CommentDto commentDto) {
        return new Comment(commentDto.getText(),
                commentDto.getItem(),
                commentDto.getAuthor(),
                commentDto.getCreated());
    }
}
