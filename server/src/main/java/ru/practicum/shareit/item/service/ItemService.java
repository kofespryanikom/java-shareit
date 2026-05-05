package ru.practicum.shareit.item.service;

import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Validated
public interface ItemService {
    Item createItem(Long userId, ItemDto itemDto);

    Item updateItem(Long itemId, Long userId, ItemPatchDto itemPatchDto);

    Item getItemById(Long itemId);

    List<Item> getItemsByUserId(Long userId);

    List<Item> searchItemsByName(String text);

    List<ItemDtoWithBookingDates> findItemsByOwner(Long ownerId);

    CommentOutDto createComment(Long owner, Long itemId, CommentDto commentDto);

    ItemDtoWithBookingDates findItemWithBookingDatesAndCommentsByItemId(Long itemId);
}
