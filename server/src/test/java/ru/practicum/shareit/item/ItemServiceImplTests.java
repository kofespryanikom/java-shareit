package ru.practicum.shareit.item;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(
        properties = "spring.datasource.url=jdbc:h2:file:./db/filmorate",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
public class ItemServiceImplTests {

    @Autowired
    private EntityManager em;
    @Autowired
    private ItemServiceImpl service;

    @Test
    void findItemsByOwnerReturnsCorrectItemsWithBookingAndComments() {
        User owner = makeUser("owner@email.com", "Owner");
        User booker = makeUser("booker@email.com", "Booker");
        Item item = makeItem("Drill", "Simple drill", owner, true);

        makeBooking(item, booker, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1), BookingStatus.APPROVED);
        makeBooking(item, booker, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), BookingStatus.APPROVED);

        makeComment("годная вещь", item, owner);

        var list = service.findItemsByOwner(owner.getId());

        assertThat(list, hasSize(1));
        var dto = list.get(0);

        assertThat(dto.getName(), equalTo("Drill"));
        assertThat(dto.getComments(), hasSize(1));
        assertThat(dto.getLastBooking(), notNullValue());
        assertThat(dto.getNextBooking(), notNullValue());
    }

    @Test
    void findItemWithBookingDatesAndCommentsByItemIdReturnsDtoWithData() {
        User owner = makeUser("user1@email.com", "User1");
        Item item = makeItem("Hammer", "For nails", owner, true);

        makeBooking(item, owner, LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(3), BookingStatus.APPROVED);
        makeBooking(item, owner, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4), BookingStatus.APPROVED);

        makeComment("good", item, owner);

        var dto = service.findItemWithBookingDatesAndCommentsByItemId(item.getId());

        assertThat(dto.getName(), equalTo("Hammer"));
        assertThat(dto.getComments(), hasSize(1));
        assertThat(dto.getLastBooking(), notNullValue());
        assertThat(dto.getNextBooking(), notNullValue());
    }

    @Test
    void createCommentValidUserCreatesComment() {
        User owner = makeUser("own@me.com", "Owner");
        User booker = makeUser("booker@me.com", "Booker");
        Item item = makeItem("Phone", "Old phone", owner, true);

        makeBooking(item, booker, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), BookingStatus.APPROVED);

        CommentDto dto = new CommentDto("работает");
        var out = service.createComment(booker.getId(), item.getId(), dto);

        assertThat(out.getText(), equalTo("работает"));
        assertThat(out.getAuthorName(), equalTo("Booker"));
        assertThat(out.getItemId(), equalTo(item.getId()));
        assertThat(out.getCreated(), notNullValue());
    }

    @Test
    void createCommentNoBookingThrowsValidation() {
        User owner = makeUser("own@me.com", "Owner");
        User notBooker = makeUser("user2@me.com", "NoBooker");
        Item item = makeItem("Screwdriver", "Cross", owner, true);

        CommentDto dto = new CommentDto("вижу но не брал");

        assertThrows(ValidationException.class,
                () -> service.createComment(notBooker.getId(), item.getId(), dto));
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

    private void makeBooking(Item item, User booker, LocalDateTime start, LocalDateTime end, BookingStatus status) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setStatus(status);
        em.persist(booking);
    }

    private void makeComment(String text, Item item, User author) {
        Comment c = new Comment();
        c.setText(text);
        c.setItem(item);
        c.setAuthor(author);
        em.persist(c);
    }
}