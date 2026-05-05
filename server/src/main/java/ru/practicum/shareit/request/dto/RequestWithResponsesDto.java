package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemNoAvailableDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class RequestWithResponsesDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemNoAvailableDto> items;
}
