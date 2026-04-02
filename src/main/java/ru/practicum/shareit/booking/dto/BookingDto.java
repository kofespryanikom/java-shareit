package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class BookingDto {
    private Long id;

    @NotNull(message = "Время начала бронирования должно быть задано")
    private LocalDateTime start;

    @NotNull(message = "Время окончания бронирования должно быть задано")
    private LocalDateTime end;

    @NotNull(message = "Вещь для бронирования должна быть задана")
    private Item item;

    @NotNull(message = "Заказчик вещи должен быть задан")
    private User booker;

    @NotNull(message = "Статус брони должен быть задан")
    private BookingStatus status;
}
