package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;


import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerIT {


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;


    @SneakyThrows
    @Test
    void add() {
        UserDto userDto = new UserDto(null, "Jon", "jon@mail.ru");
        when(userService.add(userDto)).thenReturn(userDto);

        String result = mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(userDto), result);

    }

    @SneakyThrows
    @Test
    void update() {
        Long userId = 1L;
        User user = new User("Jon", "Jon@mail.ru");
        UserDto userDto = new UserDto(null, "Jon", "Jon@mail.ru");

        when(userService.update(userId, userDto)).thenReturn(userDto);

        mockMvc.perform(patch("/users/{userId}", userId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());

        String result = String.valueOf(mockMvc.perform(patch("/users/{userId}", userId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString());


        Assertions.assertEquals(objectMapper.writeValueAsString(userDto), result);
    }

    @SneakyThrows
    @Test
    void getUser() {

        long id = 1L;
        mockMvc.perform(get("/users/{userId}", id))
                .andDo(print())
                .andExpect(status().isOk());
        verify(userService).get(id);

    }

    @SneakyThrows
    @Test
    void deleteUser() {
        Long userId = 1L;

        mockMvc.perform(delete("/users/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService, Mockito.times(1)).delete(userId);
    }

    @SneakyThrows
    @Test
    void getAll() {
        mockMvc.perform(get(String.valueOf("/users")))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).getAll();

    }
}