package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public Item createItem(@Valid @RequestBody ItemDto itemDto,
                           @RequestHeader("X-Sharer-User-Id")
                           Long owner) {
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
    public Item getItemById(@PathVariable Long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<Item> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long owner) {
        return itemService.getItemsByUserId(owner);
    }

    @GetMapping("/search")
    public List<Item> searchItemsByName(@RequestParam String text) {
        return itemService.searchItemsByName(text);
    }

}
