package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingDates;
import ru.practicum.shareit.item.dto.ItemNoAvailableDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.storage.RequestRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ItemMapper {
    private final UserService userService;
    private final RequestRepository requestRepository;

    public Item toItem(Long itemId, Long owner, ItemDto itemDto) {
        return new Item(itemId, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(),
                userService.getUserById(owner),
                itemDto.getRequestId() == null ? null : getRequestById(itemDto.getRequestId()));
    }

    public ItemDtoWithBookingDates toItemDtoWithBookingDates(Item item, LocalDateTime start, LocalDateTime end,
                                                             List<Comment> comments) {
        return new ItemDtoWithBookingDates(item.getId(), item.getName(), item.getDescription(), item.isAvailable(),
                item.getRequest() == null ? null : item.getRequest().getId(), start, end, comments);
    }

    public ItemNoAvailableDto toItemNoAvailableDto(Item item) {
        return new ItemNoAvailableDto(item.getName(), item.getDescription(), item.getOwner().getId());
    }

    private Request getRequestById(Long requestId) {
        return requestRepository.findById(requestId).orElseGet(() -> {
            log.warn("Запрос с requestId = {} не найден", requestId);
            throw new NotFoundException("Запрос с requestId = " + requestId + " не найден");
        });
    }
}
