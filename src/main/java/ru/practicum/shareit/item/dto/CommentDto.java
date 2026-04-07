package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    @NotBlank
    private String text;
    private final LocalDateTime created = LocalDateTime.now();
}
