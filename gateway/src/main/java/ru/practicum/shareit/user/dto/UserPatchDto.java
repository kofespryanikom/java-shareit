package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserPatchDto {
    private String name;

    @Email(message = "Формат почты должен содержать символ @")
    private String email;
}
