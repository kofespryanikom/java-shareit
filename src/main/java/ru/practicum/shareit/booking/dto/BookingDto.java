package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    @NotNull(message = "Время начала бронирования должно быть задано")
    private LocalDateTime start;

    @NotNull(message = "Время окончания бронирования должно быть задано")
    private LocalDateTime end;

    @NotNull(message = "Вещь для бронирования должна быть задана")
    private Long itemId;
    private Long bookerId;
    private BookingStatus statusId;
}
