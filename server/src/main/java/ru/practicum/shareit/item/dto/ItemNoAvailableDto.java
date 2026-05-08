package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemNoAvailableDto {
    private String name;
    private String description;
    private Long ownerId;
}
