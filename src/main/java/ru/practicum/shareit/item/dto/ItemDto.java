package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemDto {

    private Long id;
    private String name;
    private String description; //развёрнутое описание
    private Boolean available;  //статус о том, доступна или нет вещь для аренды
    private Long request;

    public ItemDto(Long id, String name, String description, Boolean available, Long request) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.request = request;
    }
}
