package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemWithTimeDto {

    private Long id;
    private String name;
    private String description; //развёрнутое описание
    private Boolean available;  //статус о том, доступна или нет вещь для аренды
    private Long owner;
    private Long request;
    private LocalDateTime nearStart;
    private LocalDateTime nearEnd;

    public ItemWithTimeDto(Long id, String name, String description, Boolean available, Long owner, Long request,
                           LocalDateTime nearStart, LocalDateTime nearEnd) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
        this.request = request;
        this.nearStart = nearStart;
        this.nearEnd = nearEnd;
    }
}