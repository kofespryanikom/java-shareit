package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserPatchDto;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Post запрос на создание пользователя");
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@Positive(message = "id должен быть положительным")
                                             @PathVariable Long userId,
                                             @Valid @RequestBody UserPatchDto userPatchDto) {
        log.info("Patch запрос на обновление пользователя с id = {}", userId);
        return userClient.updateUser(userId, userPatchDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@Positive(message = "id должен быть положительным")
                                              @PathVariable Long userId) {
        log.info("Get запрос на получение пользователя с id = {}", userId);
        return userClient.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@Positive(message = "id должен быть положительным") @PathVariable Long userId) {
        log.info("Delete запрос на удаление пользователя с id = {}", userId);
        userClient.deleteUser(userId);
    }
}
