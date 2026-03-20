package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
    @NotNull(message = "Имя должно быть задано")
    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    @NotNull(message = "Почта должна быть задана")
    @Email(message = "Формат почты должен содержать символ @")
    private String email;
}
