package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;

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
    BookingClient bookingClient;

    @Autowired
    private MockMvc mvc;

    private final BookingDto bookingDto = new BookingDto(
            LocalDateTime.of(2026, 5, 2, 12, 0, 0, 0),
            LocalDateTime.of(2026, 5, 3, 12, 0, 0, 0),
            1L
    );

    private final BookingDto invalidBookingDto = new BookingDto(
            LocalDateTime.of(2026, 5, 2, 12, 0, 0, 0),
            LocalDateTime.of(2026, 5, 3, 12, 0, 0, 0),
            -1L
    );

    @Test
    public void createValidBooking() throws Exception {
        String bookingDtoStartWithZerosInTheEnd = bookingDto.getStart().toString() + ":00";
        String bookingDtoEndWithZerosInTheEnd = bookingDto.getEnd().toString() + ":00";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        when(bookingClient.createBooking(anyLong(), any()))
                .thenReturn(new ResponseEntity<>(bookingDto, headers, HttpStatus.OK));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start", is(bookingDtoStartWithZerosInTheEnd)))
                .andExpect(jsonPath("$.end", is(bookingDtoEndWithZerosInTheEnd)))
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItemId()), Long.class));
    }

    @Test
    public void createInvalidBookingShouldReturnBadRequest() throws Exception {
        BookingDto invalidDto = new BookingDto();

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(invalidDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateBookingStatus() throws Exception {
        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    public void updateBookingStatusAndInvalidUserId() throws Exception {
        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", -1))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getBookingById() throws Exception {
        String bookingDtoStartWithZerosInTheEnd = bookingDto.getStart().toString() + ":00";

        when(bookingClient.getBooking(anyLong(), anyLong()))
                .thenReturn(new ResponseEntity<>(bookingDto, HttpStatus.OK));

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoStartWithZerosInTheEnd)));
    }

    @Test
    public void getBookingByInvalidId() throws Exception {
        when(bookingClient.getBooking(anyLong(), anyLong()))
                .thenReturn(new ResponseEntity<>(bookingDto, HttpStatus.OK));

        mvc.perform(get("/bookings/{bookingId}", -1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getAllBookingsByUser() throws Exception {
        List<BookingDto> bookings = List.of(bookingDto, new BookingDto(
                bookingDto.getStart(),
                bookingDto.getEnd(),
                2L
        ));

        when(bookingClient.findUsersBookings(anyLong(), any()))
                .thenReturn(new ResponseEntity<>(bookings, HttpStatus.OK));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].itemId", is(bookingDto.getItemId()), Long.class));
    }

    @Test
    public void getAllBookingsByInvalidUser() throws Exception {
        List<BookingDto> bookings = List.of(bookingDto, new BookingDto(
                bookingDto.getStart(),
                bookingDto.getEnd(),
                2L
        ));

        when(bookingClient.findUsersBookings(anyLong(), any()))
                .thenReturn(new ResponseEntity<>(bookings, HttpStatus.OK));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", -2)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getAllBookingsByOwner() throws Exception {
        List<BookingDto> bookings = List.of(bookingDto);

        when(bookingClient.findBookingsOfOwnersItemsByOwnerId(any(), anyLong()))
                .thenReturn(new ResponseEntity<>(bookings, HttpStatus.OK));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].itemId", is(bookingDto.getItemId()), Long.class));
    }

    @Test
    public void getAllBookingsByInvalidOwner() throws Exception {
        List<BookingDto> bookings = List.of(bookingDto);

        when(bookingClient.findBookingsOfOwnersItemsByOwnerId(any(), anyLong()))
                .thenReturn(new ResponseEntity<>(bookings, HttpStatus.OK));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", -1)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
    }
}