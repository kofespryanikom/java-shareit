package ru.practicum.shareit.booking.service;

import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Validated
public interface BookingService {
    Booking createBooking(@Positive(message = "id арендатора должно быть положительным") Long bookerId,
                          BookingDto bookingDto);

    Booking updateBookingStatus(@Positive(message = "id бронирования должно быть положительным") Long bookingId,
                                Boolean isApproved,
                                @Positive(message = "id пользователя должно быть положительным") Long userId);

    Booking getBookingByOwnerOrBooker(@Positive(message = "ii бронирования ") Long bookingId,
                                      @Positive Long bookerId);

    List<Booking> findUsersBookings(String state,
                                    @Positive(message = "id пользователя должно быть положительным") Long userId);

    List<Booking> findBookingsOfOwnersItemsByOwnerId(String state,
                                                     @Positive(message = "id пользователя должно быть положительным")
                                                     Long ownerId);
}
