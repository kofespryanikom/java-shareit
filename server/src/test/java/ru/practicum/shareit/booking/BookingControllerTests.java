package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTests {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private final BookingDto bookingDto = new BookingDto(
            LocalDateTime.of(2026, 5, 2, 12, 0, 0, 0),
            LocalDateTime.of(2026, 5, 3, 12, 0, 0, 0),
            1L
    );

    @Test
    public void createValidBooking() throws Exception {
        String bookingDtoStartWithZerosInTheEnd = bookingDto.getStart().toString() + ":00";
        String bookingDtoEndWithZerosInTheEnd = bookingDto.getEnd().toString() + ":00";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        Booking booking = new Booking(
                1L,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                null,
                null,
                null
        );
        when(bookingService.createBooking(anyLong(), any()))
                .thenReturn(booking);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start", is(bookingDtoStartWithZerosInTheEnd)))
                .andExpect(jsonPath("$.end", is(bookingDtoEndWithZerosInTheEnd)));
    }

    @Test
    public void updateBookingStatus() throws Exception {
        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    public void getBookingById() throws Exception {
        String bookingDtoStartWithZerosInTheEnd = bookingDto.getStart().toString() + ":00";

        Booking booking = new Booking(
                1L,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                null,
                null,
                null
        );
        when(bookingService.getBookingByOwnerOrBooker(anyLong(), anyLong()))
                .thenReturn(booking);

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start", is(bookingDtoStartWithZerosInTheEnd)));
    }

    @Test
    public void getAllBookingsByUser() throws Exception {
        List<Booking> bookings = List.of(
                new Booking(1L, bookingDto.getStart(), bookingDto.getEnd(), null, null, null),
                new Booking(2L, bookingDto.getStart(), bookingDto.getEnd(), null, null, null)
        );

        when(bookingService.findUsersBookings(any(), anyLong()))
                .thenReturn(bookings);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void getAllBookingsByOwner() throws Exception {
        List<Booking> bookings = List.of(
                new Booking(1L, bookingDto.getStart(), bookingDto.getEnd(), null, null, null)
        );

        when(bookingService.findBookingsOfOwnersItemsByOwnerId(any(), anyLong()))
                .thenReturn(bookings);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}