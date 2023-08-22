package ru.practicum.shareit.item.dto;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ItemDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long request;

}
