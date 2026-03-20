package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserPatchDto {
    private String name;

    @Email(message = "Формат почты должен содержать символ @")
    private String email;
}
