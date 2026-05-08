package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> findUsersBookings(@RequestParam(defaultValue = "all") String state,
                                                    @Positive(message = "id пользователя должен быть положительным")
                                                    @RequestHeader(USER_ID_HEADER) Long userId) {

        log.info("Get запрос на получение бронирований пользователя userId = {} с bookingState = {}", userId, state);
        return bookingClient.findUsersBookings(userId, state);
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(@Positive(message = "id арендатора должен быть положительным")
                                                @RequestHeader(USER_ID_HEADER) long userId,
                                                @RequestBody @Valid BookingDto bookingDto) {
        log.info("Post запрос на создание бронирования пользователем с userId = {}", userId);
        return bookingClient.createBooking(userId, bookingDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findByOwnerOrBooker(@Positive @RequestHeader(USER_ID_HEADER) long userId,
                                                      @Positive(message = "id бронирования должен быть положительным")
                                                      @PathVariable Long bookingId) {
        log.info("Get запрос на получение booking с id = {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> changeBookingStatus(@Positive(message = "id бронирования должен быть положительным")
                                                      @PathVariable Long bookingId,
                                                      @RequestParam Boolean approved,
                                                      @Positive(message = "id пользователя должен быть положительным")
                                                      @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Patch запрос на обновление статуса бронирования с bookingId = {}", bookingId);
        return bookingClient.updateBookingStatus(bookingId, approved, userId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findBookingsOfOwnersItemsByOwnerId(@RequestParam(defaultValue = "all") String state,
                                                                     @Positive(message = "id пользователя должен " +
                                                                             "быть положительным")
                                                                     @RequestHeader(USER_ID_HEADER) Long ownerId) {

        log.info("Get запрос на получение бронирований вещей владельца по ownerId = {}", ownerId);
        return bookingClient.findBookingsOfOwnersItemsByOwnerId(state, ownerId);
    }
}
