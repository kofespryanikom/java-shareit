package ru.practicum.shareit.booking.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    private final UserService userService;
    private final ItemService itemService;

    public Booking toBooking(Long bookingId, Long bookerId, BookingStatus bookingStatus, BookingDto bookingDto) {
        return new Booking(bookingId, bookingDto.getStart(), bookingDto.getEnd(),
                itemService.getItemById(bookingDto.getItemId()), userService.getUserById(bookerId), bookingStatus);
    }
}
