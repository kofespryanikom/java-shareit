package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Validated
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public Item createItem(Long userId, ItemDto itemDto) {
        if (!userStorage.getUsersIds().contains(userId)) {
            log.warn("Пользователя добавляющего вещь не существует!");
            throw new NotFoundException("Пользователя добавляющего вещь не существует!");
        }

        Item item = ItemMapper.toItem(null, userId, itemDto);

        return itemStorage.createItem(item);
    }

    public Item updateItem(Long itemId, Long userId, ItemPatchDto itemPatchDto) {
        if (itemPatchDto.getName() != null && itemPatchDto.getName().isBlank() ||
                itemPatchDto.getDescription() != null && itemPatchDto.getDescription().isBlank()) {
            log.warn("Поля name и description должны содержать символы отличные от пробелов");
            throw new ValidationException("Поля name и description должны содержать символы отличные от пробелов");
        }

        if (!userId.equals(getItemById(itemId).getOwner())) {
            log.warn("Обновлять вещь может только владелец вещи");
            throw new NotFoundException("Обновлять вещь может только владелец вещи");
        }
        return itemStorage.updateItem(itemId, itemPatchDto);
    }

    public Item getItemById(Long itemId) {
        return itemStorage.getItemById(itemId);
    }

    public List<Item> getItemsByUserId(Long userId) {
        return itemStorage.getItemsByUserId(userId);
    }

    public List<Item> searchItemsByName(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }

        List<Item> items = itemStorage.getAllItems();

        return items.stream()
                .filter(item -> (item.getName().toUpperCase().contains(text) ||
                        item.getDescription().toUpperCase().contains(text)) &&
                        item.isAvailable())
                .toList();
    }
}
