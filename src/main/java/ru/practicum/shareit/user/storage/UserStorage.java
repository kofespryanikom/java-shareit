package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.model.User;

import java.util.Set;

public interface UserStorage {
    User createUser(UserDto userDto);

    User getUserById(Long id);

    Set<Long> getUsersIds();

    User updateUser(Long userId, UserPatchDto userPatchDto);

    Set<String> getUsersEmails();

    void deleteUser(Long userId);
}
