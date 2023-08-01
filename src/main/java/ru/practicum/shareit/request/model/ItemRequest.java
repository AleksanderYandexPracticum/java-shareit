package ru.practicum.shareit.request.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequest {
    private Long id;
    private String description;  //текст запроса, содержащий описание требуемой вещи
    private Long requestor;  //пользователь, создавший запрос
    private LocalDateTime created;  //дата и время создания запроса

    public ItemRequest(Long id, String description, Long requestor, LocalDateTime created) {
        this.id = id;
        this.description = description;
        this.requestor = requestor;
        this.created = created;
    }
}

