package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public Item createItem(@RequestBody ItemDto itemDto,
                           @RequestHeader(USER_ID_HEADER) Long owner) {
        return itemService.createItem(owner, itemDto);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestBody ItemPatchDto itemPatchDto,
                           @PathVariable Long itemId,
                           @RequestHeader(USER_ID_HEADER)
                           Long userId) {
        return itemService.updateItem(itemId, userId, itemPatchDto);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBookingDates getItemById(@PathVariable Long itemId) {
        return itemService.findItemWithBookingDatesAndCommentsByItemId(itemId);
    }

    @GetMapping
    public List<ItemDtoWithBookingDates> getItemsByOwnerId(@RequestHeader(USER_ID_HEADER) Long owner) {
        return itemService.findItemsByOwner(owner);
    }

    @GetMapping("/search")
    public List<Item> searchItemsByName(@RequestParam String text) {
        return itemService.searchItemsByName(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentOutDto createComment(@RequestHeader(USER_ID_HEADER) Long owner,
                                       @PathVariable Long itemId,
                                       @RequestBody CommentDto commentDto) {
        return itemService.createComment(owner, itemId, commentDto);
    }
}
