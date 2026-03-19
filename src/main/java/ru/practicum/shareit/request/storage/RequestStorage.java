package ru.practicum.shareit.request.storage;

import ru.practicum.shareit.request.model.ItemRequest;

public interface RequestStorage {

    ItemRequest createItemRequest(String description, Long userId);

    ItemRequest getItemRequestById(Long id);
}
