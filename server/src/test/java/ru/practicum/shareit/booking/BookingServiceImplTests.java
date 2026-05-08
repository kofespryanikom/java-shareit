package ru.practicum.shareit.booking;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(
        properties = "spring.datasource.url=jdbc:h2:file:./db/filmorate",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
public class BookingServiceImplTests {

    @Autowired
    private EntityManager em;
    @Autowired
    private BookingServiceImpl service;

    @Test
    void createBookingCreatesNewBooking() {
        User owner = makeUser("owner@f.com", "Owner");
        Item item = makeItem("Item1", "desc", owner, true);
        User booker = makeUser("booker@f.com", "Booker");
        BookingDto dto = makeBookingDto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item.getId());

        Booking booking = service.createBooking(booker.getId(), dto);

        TypedQuery<Booking> query = em.createQuery(
                "select b from Booking b where b.id = :id", Booking.class);

        Booking found = query.setParameter("id", booking.getId()).getSingleResult();

        assertThat(found.getId(), notNullValue());
        assertThat(found.getBooker().getId(), equalTo(booker.getId()));
        assertThat(found.getItem().getId(), equalTo(item.getId()));
        assertThat(found.getStatus(), equalTo(BookingStatus.WAITING));
        assertThat(found.getStart(), equalTo(dto.getStart()));
        assertThat(found.getEnd(), equalTo(dto.getEnd()));
    }

    @Test
    void updateBookingStatusOwnerApprovesChangesStatus() {
        User owner = makeUser("own@f.com", "Owner");
        User booker = makeUser("book@f.com", "Booker");
        Item item = makeItem("Item2", "item2", owner, true);
        Booking booking = makeBooking(item, booker, BookingStatus.WAITING);

        Booking updated = service.updateBookingStatus(booking.getId(), true, owner.getId());

        TypedQuery<Booking> query = em.createQuery(
                "select b from Booking b where b.id = :id", Booking.class);
        Booking found = query.setParameter("id", booking.getId()).getSingleResult();

        assertThat(found.getStatus(), equalTo(BookingStatus.APPROVED));
        assertThat(updated.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void updateBookingStatusNotOwnerThrowsValidationException() {
        User owner = makeUser("owner@f.com", "Owner");
        User booker = makeUser("booker@f.com", "Booker");
        User other = makeUser("oth@f.com", "Other");
        Item item = makeItem("Item3", "desc", owner, true);
        Booking booking = makeBooking(item, booker, BookingStatus.WAITING);

        assertThrows(ValidationException.class,
                () -> service.updateBookingStatus(booking.getId(), true, other.getId()));
    }

    @Test
    void getBookingByOwnerOrBookerOwnerOrBookerCanGetBooking() {
        User owner = makeUser("o@f.com", "O");
        User booker = makeUser("b@f.com", "B");
        Item item = makeItem("I", "D", owner, true);
        Booking booking = makeBooking(item, booker, BookingStatus.WAITING);

        Booking byOwner = service.getBookingByOwnerOrBooker(booking.getId(), owner.getId());
        Booking byBooker = service.getBookingByOwnerOrBooker(booking.getId(), booker.getId());

        assertThat(byOwner.getId(), equalTo(booking.getId()));
        assertThat(byBooker.getId(), equalTo(booking.getId()));
    }

    @Test
    void getBookingByOwnerOrBookerNotRelatedUserThrowsValidation() {
        User owner = makeUser("o@f.com", "O");
        User booker = makeUser("b@f.com", "B");
        User other = makeUser("oth@f.com", "Other");
        Item item = makeItem("I", "D", owner, true);
        Booking booking = makeBooking(item, booker, BookingStatus.WAITING);

        assertThrows(ValidationException.class,
                () -> service.getBookingByOwnerOrBooker(booking.getId(), other.getId()));
    }

    private User makeUser(String email, String name) {
        User u = new User();
        u.setEmail(email);
        u.setName(name);
        em.persist(u);
        return u;
    }

    private Item makeItem(String name, String desc, User owner, boolean available) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(desc);
        item.setOwner(owner);
        item.setAvailable(available);
        em.persist(item);
        return item;
    }

    private Booking makeBooking(Item item, User booker, BookingStatus status) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(status);
        em.persist(booking);
        return booking;
    }

    private BookingDto makeBookingDto(LocalDateTime start, LocalDateTime end, Long itemId) {
        BookingDto dto = new BookingDto();
        dto.setStart(start);
        dto.setEnd(end);
        dto.setItemId(itemId);
        return dto;
    }
}
