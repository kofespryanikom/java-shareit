package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> usersMap;
    private Long userId;

    public InMemoryUserStorage() {
        usersMap = new HashMap<>();
        userId = 0L;
    }

    public User createUser(UserDto userDto) {
        String name = userDto.getName();
        String email = userDto.getEmail();

        Long id = getUserId();
        User userToAdd = new User(id, name, email);
        usersMap.put(id, userToAdd);

        log.info("Создан пользователь с id = {}", id);

        return userToAdd;
    }

    public Set<Long> getUsersIds() {
        return usersMap.values().stream()
                .map(User::getId).collect(Collectors.toSet());
    }

    public Long getUserId() {
        return ++userId;
    }

    public User getUserById(Long id) {
        return usersMap.get(id);
    }

    public User updateUser(Long userId, UserPatchDto userPatchDto) {
        User user = getUserById(userId);
        String email = userPatchDto.getEmail() == null ? user.getEmail() : userPatchDto.getEmail();
        String name = userPatchDto.getName() == null ? user.getName() : userPatchDto.getName();

        user.setEmail(email);
        user.setName(name);

        usersMap.put(userId, user);

        log.info("Обновлен пользователь с id = {}", userId);

        return user;
    }

    public Set<String> getUsersEmails() {
        return usersMap.values().stream()
                .map(User::getEmail).collect(Collectors.toSet());
    }

    public void deleteUser(Long userId) {
        usersMap.remove(userId);
    }
}
