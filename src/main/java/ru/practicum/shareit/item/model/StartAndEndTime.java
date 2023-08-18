package ru.practicum.shareit.item.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StartAndEndTime {
    private LocalDateTime nearStart;
    private LocalDateTime nearEnd;

    public StartAndEndTime(LocalDateTime nearStart, LocalDateTime nearEnd) {
        this.nearStart = nearStart;
        this.nearEnd = nearEnd;
    }
}