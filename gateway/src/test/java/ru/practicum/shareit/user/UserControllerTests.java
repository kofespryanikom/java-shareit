package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserPatchDto;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTests {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserClient userClient;

    @Autowired
    private MockMvc mvc;

    private final UserDto userDto = new UserDto(
            "John",
            "Doe@mail.ru"
    );

    @Test
    public void saveUser() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        when(userClient.createUser(any()))
                .thenReturn(new ResponseEntity<>(userDto, headers, HttpStatus.OK));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    public void saveInvalidUser() throws Exception {
        UserDto invalidUserDto = new UserDto(
                "",
                "Doe@mail.ru"
        );

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(invalidUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUser() throws Exception {
        UserPatchDto userPatchDto = new UserPatchDto(
                "Ivan",
                null
        );
        UserDto updatedUserDto = new UserDto(
                "Ivan",
                "Doe@mail.ru"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        when(userClient.updateUser(anyLong(), any()))
                .thenReturn(new ResponseEntity<>(updatedUserDto, headers, HttpStatus.OK));

        mvc.perform(patch("/users/{id}", 1)
                        .content(mapper.writeValueAsString(userPatchDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(updatedUserDto.getName())))
                .andExpect(jsonPath("$.email", is(updatedUserDto.getEmail())));
    }

    @Test
    public void deleteUser() throws Exception {
        mvc.perform(delete("/users/{id}", 1))
                .andExpect(status().isOk());
    }

    @Test
    public void getUserById() throws Exception {
        when(userClient.getUserById(anyLong()))
                .thenReturn(new ResponseEntity<>(userDto, HttpStatus.OK));

        mvc.perform(get("/users/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    public void getUserByInvalidId() throws Exception {
        mvc.perform(get("/users/{id}", -1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
