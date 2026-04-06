package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_IdAndStartBeforeAndEndAfterAndStatusOrderByStartDesc(Long bookerId,
                                                                                    LocalDateTime now,
                                                                                    LocalDateTime sameNow,
                                                                                    BookingStatus bookingStatus);

    List<Booking> findByBooker_IdAndEndBeforeAndStatusOrderByStartDesc(Long bookerId,
                                                                       LocalDateTime now,
                                                                       BookingStatus bookingStatus);

    List<Booking> findByBooker_IdAndStartAfterAndStatusOrderByStartDesc(Long bookerId,
                                                                        LocalDateTime now,
                                                                        BookingStatus bookingStatus);

    List<Booking> findByBooker_IdAndStatusOrderByStartDesc(Long bookerId, BookingStatus bookingStatus);

    List<Booking> findByBooker_IdOrderByStartDesc(Long bookerId);

    List<Booking> findByItem_Owner_IdAndStartBeforeAndEndAfterAndStatusOrderByStartDesc(Long ownerId,
                                                                                         LocalDateTime now,
                                                                                         LocalDateTime sameNow,
                                                                                         BookingStatus status);

    List<Booking> findByItem_Owner_IdAndEndBeforeAndStatusOrderByStartDesc(Long ownerId,
                                                                           LocalDateTime now,
                                                                           BookingStatus status);

    List<Booking> findByItem_Owner_IdAndStartAfterAndStatusOrderByStartDesc(Long ownerId,
                                                                            LocalDateTime now,
                                                                            BookingStatus status);

    List<Booking> findByItem_Owner_IdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    List<Booking> findByItem_Owner_IdOrderByStartDesc(Long ownerId);

    Booking findFirstByItem_IdAndEndBeforeAndStatusOrderByEndDesc(Long itemId,
                                                                  LocalDateTime now,
                                                                  BookingStatus status);

    Booking findFirstByItem_IdAndStartAfterAndStatusOrderByStart(Long itemId, LocalDateTime now, BookingStatus status);
}
