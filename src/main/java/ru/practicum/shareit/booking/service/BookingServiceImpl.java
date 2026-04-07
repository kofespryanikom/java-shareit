package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingMapper bookingMapper;

    @Transactional
    public Booking createBooking(Long bookerId, BookingDto bookingDto) {
        userService.checkDoesUserExist(bookerId);
        Item item = itemService.getItemById(bookingDto.getItemId());

        if (!item.isAvailable()) {
            log.warn("Вещь с id = {} не является доступной для брони", bookingDto.getItemId());
            throw new ValidationException("Вещь с id = " + bookingDto.getItemId() + " не является доступной для брони");
        }

        Booking bookingToCreate = bookingMapper.toBooking(null, bookerId,
                BookingStatus.WAITING, bookingDto);

        Booking bookingCreated = bookingRepository.save(bookingToCreate);

        log.info("Создано бронирование с bookingId = {} пользователем с userId = {}", bookingCreated.getId(), bookerId);

        return bookingCreated;
    }

    @Transactional
    public Booking updateBookingStatus(Long bookingId, Boolean isApproved, Long userId) {
        Booking booking = getBookingById(bookingId);
        Item item = booking.getItem();

        if (!userId.equals(item.getOwner().getId())) {
            log.warn("Одобрять бронирование может только владелец вещи");
            throw new ValidationException("Одобрять бронирование может только владелец вещи");
        }

        BookingStatus bookingStatusUpdated = isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED;

        booking.setStatus(bookingStatusUpdated);

        return bookingRepository.save(booking);
    }

    public Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseGet(() -> {
            log.warn("Нет бронирования с id = {}", bookingId);
            throw new NotFoundException("Бронирования с id " + bookingId + " нет!");
        });
    }

    public Booking getBookingByOwnerOrBooker(Long bookingId, Long userId) {
        Booking booking = getBookingById(bookingId);

        if (!(booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId))) {
            log.warn("Пользователь с userId = {} не является владельцем забронированной вещи или не создавал " +
                    "бронирования с bookingId = {}", userId, bookingId);
            throw new ValidationException("Пользователь с userId = " + userId + " не является владельцем " +
                    "забронированной вещи или не создавал бронирования с bookingId = " + bookingId);
        }

        return booking;
    }

    public List<Booking> findUsersBookings(String state, Long userId) {
        userService.checkDoesUserExist(userId);

        if (state == null) {
            return bookingRepository.findByBooker_IdOrderByStartDesc(userId);
        }

        BookingState bookingState = BookingState.valueOf(state);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = new ArrayList<>();

        switch (bookingState) {
            case CURRENT:
                bookings = bookingRepository
                        .findByBooker_IdAndStartBeforeAndEndAfterAndStatusOrderByStartDesc(userId, now, now,
                                BookingStatus.APPROVED);
                break;
            case PAST:
                bookings = bookingRepository.findByBooker_IdAndEndBeforeAndStatusOrderByStartDesc(userId, now,
                        BookingStatus.APPROVED);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBooker_IdAndStartAfterAndStatusOrderByStartDesc(userId, now,
                        BookingStatus.APPROVED);
                break;
            case WAITING:
                bookings = bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                bookings = null;
        }

        return bookings;
    }

    public List<Booking> findBookingsOfOwnersItemsByOwnerId(String state, Long ownerId) {
        userService.checkDoesUserExist(ownerId);

        if (state == null) {
            return bookingRepository.findByItem_Owner_IdOrderByStartDesc(ownerId);
        }

        BookingState bookingState = BookingState.valueOf(state);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = new ArrayList<>();

        switch (bookingState) {
            case CURRENT:
                bookings = bookingRepository
                        .findByItem_Owner_IdAndStartBeforeAndEndAfterAndStatusOrderByStartDesc(ownerId, now, now,
                                BookingStatus.APPROVED);
                break;
            case PAST:
                bookings = bookingRepository.findByItem_Owner_IdAndEndBeforeAndStatusOrderByStartDesc(ownerId, now,
                        BookingStatus.APPROVED);
                break;
            case FUTURE:
                bookings = bookingRepository.findByItem_Owner_IdAndStartAfterAndStatusOrderByStartDesc(ownerId, now,
                        BookingStatus.APPROVED);
                break;
            case WAITING:
                bookings = bookingRepository
                        .findByItem_Owner_IdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository
                        .findByItem_Owner_IdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED);
                break;
            default:
                bookings = null;
        }

        return bookings;
    }
}
