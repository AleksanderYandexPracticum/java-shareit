package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CommentCreatedStringDto {

    private Long id;
    private String text;
    private Item item;
    private User author;
    private LocalDateTime created;
    private String authorName;
}
