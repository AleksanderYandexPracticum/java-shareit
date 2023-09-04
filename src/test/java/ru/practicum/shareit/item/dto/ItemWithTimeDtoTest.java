package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemWithTimeDtoTest {

    @Test
    void create() {
        ItemWithTimeDto itemWithTimeDto = new ItemWithTimeDto();
        itemWithTimeDto.setId(1L);
        itemWithTimeDto.setName("имя");
        itemWithTimeDto.setDescription("имя");
        itemWithTimeDto.setAvailable(true);
        itemWithTimeDto.setOwner(1L);
        itemWithTimeDto.setRequestId(1L);

        assertEquals(itemWithTimeDto.getId(), 1L);
        assertEquals(itemWithTimeDto.getName(), "имя");
        assertEquals(itemWithTimeDto.getDescription(), "имя");
        assertEquals(itemWithTimeDto.getAvailable(), true);
        assertEquals(itemWithTimeDto.getOwner(), 1L);
        assertEquals(itemWithTimeDto.getRequestId(), 1L);
    }

}