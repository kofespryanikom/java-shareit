package ru.practicum.shareit.request.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class ItemRequest {
    private final Long id;
    private final String description;
    private final User requestor;
    private LocalDateTime created = LocalDateTime.now();
}
