package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getRequestId()
        );
    }

    public static Item toItem(Long itemId, Long owner, ItemDto itemDto) {
        Item item = new Item(itemId, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), owner,
                null);
        item.setOwner(owner);
        return item;
    }
}
