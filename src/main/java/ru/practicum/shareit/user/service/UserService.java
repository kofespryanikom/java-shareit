package ru.practicum.shareit.user.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.model.User;

@Validated
public interface UserService {
    User createUser(@Valid UserDto userDto);

    User updateUser(@Positive Long userId, UserPatchDto userPatchDto);

    User getUserById(@Positive Long userId);

    void deleteUser(@Positive Long userId);
}
