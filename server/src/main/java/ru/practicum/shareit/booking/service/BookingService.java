package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking createBooking(Long bookerId,
                          BookingDto bookingDto);

    Booking updateBookingStatus(Long bookingId, Boolean isApproved, Long userId);

    Booking getBookingByOwnerOrBooker(Long bookingId, Long bookerId);

    List<Booking> findUsersBookings(String state,
                                    Long userId);

    List<Booking> findBookingsOfOwnersItemsByOwnerId(String state, Long ownerId);
}
