package ru.practicum.shareit.user.dto;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class UserDto {
    private Long id;
    private String name;
    private String email;

}
