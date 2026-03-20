package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item createItem(Item item);

    Item getItemById(Long id);

    Item updateItem(Long itemId, ItemPatchDto itemPatchDto);

    List<Item> getItemsByUserId(Long userId);

    List<Item> getAllItems();
}
