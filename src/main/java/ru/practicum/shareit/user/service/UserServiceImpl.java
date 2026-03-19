package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    public User createUser(UserDto userDto) {
        if (userStorage.getUsersEmails().contains(userDto.getEmail())) {
            log.warn("Пользователь с такой почтой {} уже существует", userDto.getEmail());
            throw new ConflictException("Пользователь с такой почтой " + userDto.getEmail() + " уже существует");
        }
        return userStorage.createUser(userDto);
    }

    public User updateUser(Long userId, UserPatchDto userPatchDto) {
        if (userPatchDto.getName() != null && userPatchDto.getName().isBlank()) {
            log.warn("Поле name должно содержать символы отличные от пробелов");
            throw new ValidationException("Поле name должно содержать символы отличные от пробелов");
        }

        if (userStorage.getUsersEmails().contains(userPatchDto.getEmail())) {
            log.warn("Пользователь с такой почтой {} уже существует", userPatchDto.getEmail());
            throw new ConflictException("Пользователь с такой почтой " + userPatchDto.getEmail() + " уже существует");
        }

        return userStorage.updateUser(userId, userPatchDto);
    }

    public User getUserById(Long userId) {
        return userStorage.getUserById(userId);
    }

    public void deleteUser(Long userId) {
        if (!userStorage.getUsersIds().contains(userId)) {
            log.warn("Пользователя с id = {} не существует", userId);
            throw new NotFoundException("Пользователя с id = " + userId + " не существует");
        }
        userStorage.deleteUser(userId);
    }
}
