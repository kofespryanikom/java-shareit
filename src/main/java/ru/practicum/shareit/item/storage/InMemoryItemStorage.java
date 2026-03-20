package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class InMemoryItemStorage implements ItemStorage {

    private final Map<Long, Item> itemMap;
    private Long itemId;

    public InMemoryItemStorage() {
        itemMap = new HashMap<>();
        itemId = 0L;
    }

    public Item createItem(Item item) {
        String name = item.getName();
        String description = item.getDescription();
        boolean available = item.isAvailable();
        Long userId = item.getOwner();
        ItemRequest itemRequest = item.getRequest();

        Long id = getItemId();
        Item itemToAdd = new Item(id, name, description, available, userId, itemRequest);
        itemMap.put(id, itemToAdd);

        log.info("Создана вещь с id = {} и userId = {}", id, userId);

        return itemToAdd;
    }

    public Long getItemId() {
        return ++itemId;
    }

    public Item getItemById(Long id) {
        if (itemMap.get(id) == null) {
            log.warn("Нет вещи с id = {}", id);
            throw new NotFoundException("Вещи с id " + id + " нет!");
        }
        return itemMap.get(id);
    }

    public Item updateItem(Long itemId, ItemPatchDto itemPatchDto) {
        Item item = getItemById(itemId);
        String name = itemPatchDto.getName() == null ? item.getName() : itemPatchDto.getName();
        String description = itemPatchDto.getDescription() == null ? item.getDescription() :
                itemPatchDto.getDescription();
        boolean available = itemPatchDto.getAvailable() == null ? item.isAvailable() : itemPatchDto.getAvailable();
        ItemRequest itemRequest = itemPatchDto.getRequest() == null ? item.getRequest() : itemPatchDto.getRequest();

        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setRequest(itemRequest);

        itemMap.put(itemId, item);

        log.info("Обновлена вещь с id = {}", itemId);

        return item;
    }

    public List<Item> getItemsByUserId(Long userId) {
        return itemMap.values().stream()
                .filter(item -> item.getOwner().equals(userId))
                .toList();
    }

    public List<Item> getAllItems() {
        return new ArrayList<>(itemMap.values());
    }
}
