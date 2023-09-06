package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class UserDtoTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void testSerialize() throws IOException {
        UserDto userDto = new UserDto(null, "Jon", "jon@mail.ru");
        JsonContent<UserDto> result = this.json.write(userDto);

        assertThat(result).hasJsonPathStringValue("$.name");
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Jon");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("jon@mail.ru");
    }

    @Test
    public void testDeserialize() throws IOException {

        String jsonContent = "{\"name\": \"Jon\", \"email\":\"jon@mail.ru\"}";

        UserDto result = this.json.parse(jsonContent).getObject();

        assertThat(result.getName()).isEqualTo("Jon");
        assertThat(result.getEmail()).isEqualTo("jon@mail.ru");
    }

}