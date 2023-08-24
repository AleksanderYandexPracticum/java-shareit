package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ItemWithTimeDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long owner;
    private Long request;
    private LocalDateTime nearStart;
    private LocalDateTime nearEnd;

}