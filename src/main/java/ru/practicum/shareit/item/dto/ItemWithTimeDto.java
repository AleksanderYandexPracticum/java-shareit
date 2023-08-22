package ru.practicum.shareit.item.dto;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
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