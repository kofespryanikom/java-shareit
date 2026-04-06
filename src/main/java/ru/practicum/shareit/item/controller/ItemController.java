package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
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

    @PostMapping
    public Item createItem(@Valid @RequestBody ItemDto itemDto,
                           @RequestHeader("X-Sharer-User-Id") Long owner) {
        return itemService.createItem(owner, itemDto);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@Valid @RequestBody ItemPatchDto itemPatchDto,
                           @PathVariable Long itemId,
                           @RequestHeader("X-Sharer-User-Id")
                           Long userId) {
        return itemService.updateItem(itemId, userId, itemPatchDto);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBookingDates getItemById(@PathVariable Long itemId) {
        return itemService.findItemWithBookingDatesAndCommentsByItemId(itemId);
    }

    @GetMapping
    public List<ItemDtoWithBookingDates> getItemsByOwnerId(@RequestHeader("X-Sharer-User-Id") Long owner) {
        return itemService.findItemsByOwner(owner);
    }

    @GetMapping("/search")
    public List<Item> searchItemsByName(@RequestParam String text) {
        return itemService.searchItemsByName(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentOutDto createComment(@RequestHeader("X-Sharer-User-Id") Long owner,
                                       @PathVariable Long itemId,
                                       @Valid @RequestBody CommentDto commentDto) {
        return itemService.createComment(owner, itemId, commentDto);
    }
}
