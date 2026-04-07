package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingDates;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemMapper {
    private final UserService userService;

    public Item toItem(Long itemId, Long owner, ItemDto itemDto) {
        return new Item(itemId, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(),
                userService.getUserById(owner),
                null);
    }

    public ItemDtoWithBookingDates toItemDtoWithBookingDates(Item item, LocalDateTime start, LocalDateTime end,
                                                             List<Comment> comments) {
        return new ItemDtoWithBookingDates(item.getId(), item.getName(), item.getDescription(), item.isAvailable(),
                item.getRequestId(), start, end, comments);
    }
}
