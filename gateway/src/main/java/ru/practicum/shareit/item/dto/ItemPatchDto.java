package ru.practicum.shareit.item.dto;

import lombok.Getter;

@Getter
public class ItemPatchDto {
    private String name;
    private String description;
    private Boolean available;
    private Long request;
}
