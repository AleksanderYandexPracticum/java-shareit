package ru.practicum.shareit.item.model;

import lombok.Data;

@Data
public class Item {
    private Long id;
    private String name;
    private String description; //развёрнутое описание
    private Boolean available;  //статус о том, доступна или нет вещь для аренды
    private Long owner;  //владелец вещи
    private Long request;  //если вещь была создана по запросу другого пользователя, то в этом
    //поле будет храниться ссылка на соответствующий запрос

    public Item(String name, String description, Boolean available, Long owner, Long request) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
        this.request = request;
    }
}
