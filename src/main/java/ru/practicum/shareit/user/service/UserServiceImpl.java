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
import ru.practicum.shareit.user.storage.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public User createUser(UserDto userDto) {
        if (userRepository.findAllEmails().contains(userDto.getEmail())) {
            log.warn("Пользователь с такой почтой {} уже существует", userDto.getEmail());
            throw new ConflictException("Пользователь с такой почтой " + userDto.getEmail() + " уже существует");
        }

        User userToSave = new User();
        userToSave.setName(userDto.getName());
        userToSave.setEmail(userDto.getEmail());

        User savedUser = userRepository.save(userToSave);

        log.info("Создан пользователь с id = {}", savedUser.getId());

        return savedUser;
    }

    public User updateUser(Long userId, UserPatchDto userPatchDto) {
        if (userPatchDto.getName() != null && userPatchDto.getName().isBlank()) {
            log.warn("Поле name должно содержать символы отличные от пробелов");
            throw new ValidationException("Поле name должно содержать символы отличные от пробелов");
        }

        if (userRepository.findAllEmails().contains(userPatchDto.getEmail())) {
            log.warn("Пользователь с такой почтой {} уже существует", userPatchDto.getEmail());
            throw new ConflictException("Пользователь с такой почтой " + userPatchDto.getEmail() + " уже существует");
        }

        User user = getUserById(userId);
        String email = userPatchDto.getEmail() == null ? user.getEmail() : userPatchDto.getEmail();
        String name = userPatchDto.getName() == null ? user.getName() : userPatchDto.getName();

        user.setEmail(email);
        user.setName(name);

        User userUpdated = userRepository.save(user);

        log.info("Обновлен пользователь с id = {}", userId);

        return userUpdated;
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseGet(() -> {
            log.warn("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id = " +
                    userId + " не найден");
        });
    }

    public void deleteUser(Long userId) {
        checkDoesUserExist(userId);
        userRepository.deleteById(userId);
    }

    public void checkDoesUserExist(Long userId) {
        userRepository.findById(userId).orElseGet(() -> {
            log.warn("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id = " +
                userId + " не найден");
        });
    }
}
