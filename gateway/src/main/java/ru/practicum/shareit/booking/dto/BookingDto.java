package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
	@NotNull(message = "Время начала бронирования должно быть задано")
	private LocalDateTime start;

	@NotNull(message = "Время окончания бронирования должно быть задано")
	private LocalDateTime end;

	@Positive(message = "id вещи для бронирования должен быть положительным")
	private Long itemId;
}
