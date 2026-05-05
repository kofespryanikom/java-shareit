package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.model.User;

public interface UserService {
    User createUser(UserDto userDto);

    User updateUser(Long userId, UserPatchDto userPatchDto);

    User getUserById(Long userId);

    void deleteUser(Long userId);

    void checkDoesUserExist(Long userId);
}
