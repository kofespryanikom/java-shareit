package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public Booking createBooking(@RequestBody BookingDto bookingDto,
                                 @RequestHeader(USER_ID_HEADER) Long owner) {
        return bookingService.createBooking(owner, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public Booking changeBookingStatus(@PathVariable Long bookingId,
                                       @RequestParam Boolean approved,
                                       @RequestHeader(USER_ID_HEADER) Long userId) {
        return bookingService.updateBookingStatus(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public Booking findByOwnerOrBooker(@PathVariable Long bookingId, @RequestHeader(USER_ID_HEADER) Long userId) {
        return bookingService.getBookingByOwnerOrBooker(bookingId, userId);
    }

    @GetMapping
    public List<Booking> findUsersBookings(@RequestParam(required = false) String state,
                                           @RequestHeader(USER_ID_HEADER) Long userId) {
        return bookingService.findUsersBookings(state, userId);
    }

    @GetMapping("/owner")
    public List<Booking> findBookingsOfOwnersItemsByOwnerId(@RequestParam(required = false) String state,
                                                            @RequestHeader(USER_ID_HEADER) Long ownerId) {
        return bookingService.findBookingsOfOwnersItemsByOwnerId(state, ownerId);
    }
}
