package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@EqualsAndHashCode
public class ItemRequestDto {

    private Long id;
    private String description;
    private Long requestor;
    private LocalDateTime created;
    private List<ItemDto> items;
}
