package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemDtoWithBookingDates {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long request;
    private LocalDateTime lastBooking;
    private LocalDateTime nextBooking;
    private List<Comment> comments;
}
