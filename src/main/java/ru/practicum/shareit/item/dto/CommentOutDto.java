package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class CommentOutDto {
    private final Long id;
    private final String text;
    private final Long itemId;
    private final String authorName;
    private final LocalDateTime created;
}
