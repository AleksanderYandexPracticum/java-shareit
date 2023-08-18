package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Data
public class CommentDto {

    private Long id;
    private String text;
    private Long item;
    private Long author;
    private LocalDateTime created;

    public CommentDto(Long id, String text, Long item, Long author, LocalDateTime created) {
        this.id = id;
        this.text = text;
        this.item = item;
        this.author = author;
        this.created = created;
    }
}
