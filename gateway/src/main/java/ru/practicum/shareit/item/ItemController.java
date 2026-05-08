package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemClient itemClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createItem(@Valid @RequestBody ItemDto itemDto,
                                             @NotNull(message = "Заголовок X-Sharer-User-Id должен быть передан")
                                             @RequestHeader(USER_ID_HEADER) Long owner) {
        log.info("Post запрос на создание предмета владельцем с id = {}", owner);
        return itemClient.createItem(owner, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@Valid @RequestBody ItemPatchDto itemPatchDto,
                                             @Positive(message = "Значение заголовка X-Sharer-User-Id должно быть " +
                                                     "положительным")
                                             @PathVariable Long itemId,
                                             @NotNull(message = "Заголовок X-Sharer-User-Id должен быть передан")
                                             @RequestHeader(USER_ID_HEADER)
                                             Long userId) {
        log.info("Patch запрос на обновление предмета с itemId = {} владельцем с userId = {}", itemId, userId);
        return itemClient.updateItem(itemId, userId, itemPatchDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@Positive(message = "id должен быть положительным")
                                              @PathVariable Long itemId) {
        log.info("Get запрос на получение предмета по itemId = {}", itemId);
        return itemClient.getItemById(itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByOwnerId(@Positive(message = "id должен быть положительным")
                                                    @RequestHeader(USER_ID_HEADER) Long owner) {
        log.info("Get запрос на получение всех предметов владельца вещей по ownerId = {}", owner);
        return itemClient.findItemsByOwner(owner);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemsByNameOrDescription(@RequestParam String text) {
        log.info("Get запрос на поиск предметов по строке {} названию или описанию", text);
        return itemClient.searchItemsByNameOrDescription(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Positive(message = "id должен быть положительным")
                                                @RequestHeader(USER_ID_HEADER) Long owner,
                                                @Positive(message = "id должен быть положительным")
                                                @PathVariable Long itemId,
                                                @Valid @RequestBody CommentDto commentDto) {
        log.info("Post запрос на создание комментария на предмет с itemId = {}", itemId);
        return itemClient.createComment(owner, itemId, commentDto);
    }
}
