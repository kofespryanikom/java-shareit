package ru.practicum.shareit.item.service;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item createItem(@NotNull(message = "Заголовок X-Sharer-User-Id должен быть передан") Long userId, ItemDto itemDto);

    Item updateItem(Long itemId, @NotNull(message = "Заголовок X-Sharer-User-Id должен быть передан") Long userId,
                    ItemPatchDto itemPatchDto);

    Item getItemById(@Positive(message = "id должен быть положительным") Long itemId);

    List<Item> getItemsByUserId(@NotNull(message = "Заголовок X-Sharer-User-Id должен быть передан") Long userId);

    List<Item> searchItemsByName(String text);
}
