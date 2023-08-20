package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;


@Data
public class CommentCreatedStringDto {

    private Long id;
    private String text;
    private Item item;
    private User author;
    private String created;
    private String authorName;

    public CommentCreatedStringDto(Long id, String text, Item item, User author, String created) {
        this.id = id;
        this.text = text;
        this.item = item;
        this.author = author;
        this.created = created;
    }
}
