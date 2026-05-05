package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final RequestService requestService;

    @Transactional
    public Item createItem(Long userId, ItemDto itemDto) {
        userService.checkDoesUserExist(userId);

        Item item = itemMapper.toItem(null, userId, itemDto);

        return itemRepository.save(item);
    }

    @Transactional
    public Item updateItem(Long itemId, Long userId, ItemPatchDto itemPatchDto) {
        if (itemPatchDto.getName() != null && itemPatchDto.getName().isBlank() ||
                itemPatchDto.getDescription() != null && itemPatchDto.getDescription().isBlank()) {
            log.warn("Поля name и description должны содержать символы отличные от пробелов");
            throw new ValidationException("Поля name и description должны содержать символы отличные от пробелов");
        }

        if (!userId.equals(getItemById(itemId).getOwner().getId())) {
            log.warn("Обновлять вещь может только владелец вещи");
            throw new NotFoundException("Обновлять вещь может только владелец вещи");
        }

        Item itemToUpdate = getItemById(itemId);
        itemToUpdate.setAvailable(itemPatchDto.getAvailable() == null ? itemToUpdate.isAvailable()
                : itemPatchDto.getAvailable());
        itemToUpdate.setName(itemPatchDto.getName() == null ? itemToUpdate.getName() : itemPatchDto.getName());
        itemToUpdate.setDescription(itemPatchDto.getDescription() == null ? itemToUpdate.getDescription()
                : itemPatchDto.getDescription());

        Item itemUpdated = itemRepository.save(itemToUpdate);

        log.info("Обновлена вещь с id = {}", itemId);

        return itemUpdated;
    }

    public Item getItemById(Long itemId) {
        return itemRepository.findById(itemId).orElseGet(() -> {
            log.warn("Нет вещи с id = {}", itemId);
            throw new NotFoundException("Вещи с id " + itemId + " нет!");
        });
    }

    public List<Item> getItemsByUserId(Long userId) {
        return itemRepository.findByOwner_Id(userId);
    }

    public List<Item> searchItemsByName(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }

        return itemRepository.searchItems(text);
    }

    public List<ItemDtoWithBookingDates> findItemsByOwner(Long ownerId) {
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookingsCompletedByUser = bookingRepository
                .findByItem_Owner_IdAndEndBeforeAndStatusOrderByStartDesc(ownerId, now, BookingStatus.APPROVED);
        Map<Long, Booking> pastItemsIdsAndTheirBookingsMap = bookingsCompletedByUser.stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), Function.identity()));

        List<Booking> futureBookingsByUser = bookingRepository
                .findByItem_Owner_IdAndStartAfterAndStatusOrderByStartDesc(ownerId, now, BookingStatus.APPROVED);
        Map<Long, Booking> futureItemsIdsAndTheirBookingsMap = futureBookingsByUser.stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), Function.identity()));


        List<Item> itemsOfUser = getItemsByUserId(ownerId);

        List<ItemDtoWithBookingDates> itemDtosOfUser = itemsOfUser.stream()
                .map(item -> itemMapper.toItemDtoWithBookingDates(item, null, null, new ArrayList<>()))
                .toList();
        Map<Long, ItemDtoWithBookingDates> itemDtosOfUserMap = itemDtosOfUser.stream()
                .collect(Collectors.toMap(ItemDtoWithBookingDates::getId, Function.identity()));

        Set<Long> uniqueBookedItemsIdsPast = new HashSet<>(pastItemsIdsAndTheirBookingsMap.keySet());

        for (Long itemId : uniqueBookedItemsIdsPast) {
            List<Booking> bookingsToSort = new ArrayList<>();

            for (Booking booking : bookingsCompletedByUser) {
                if (booking.getItem().getId().equals(itemId)) {
                    bookingsToSort.add(booking);
                }
            }

            Booking lastBooking = bookingsToSort.stream()
                    .max(Comparator.comparing(Booking::getEnd))
                    .orElse(null);

            LocalDateTime lastBookingDateTime = lastBooking == null ? null : lastBooking.getEnd();

            itemDtosOfUserMap.get(itemId).setLastBooking(lastBookingDateTime);
        }

        Set<Long> uniqueBookedItemsIdsFuture = new HashSet<>(futureItemsIdsAndTheirBookingsMap.keySet());

        for (Long itemId : uniqueBookedItemsIdsFuture) {
            List<Booking> bookingsToSort = new ArrayList<>();

            for (Booking booking : futureBookingsByUser) {
                if (booking.getItem().getId().equals(itemId)) {
                    bookingsToSort.add(booking);
                }
            }

            Booking nearestBooking = bookingsToSort.stream()
                    .min(Comparator.comparing(Booking::getStart))
                    .orElse(null);

            LocalDateTime nearestBookingDateTime = nearestBooking == null ? null : nearestBooking.getStart();

            itemDtosOfUserMap.get(itemId).setNextBooking(nearestBookingDateTime);
        }

        List<Comment> allComments = commentRepository.findAll();

        for (Comment comment : allComments) {
            if (itemDtosOfUserMap.containsKey(comment.getItem().getId())) {
                itemDtosOfUserMap.get(comment.getItem().getId()).getComments().add(comment);
            }
        }

        return new ArrayList<>(itemDtosOfUserMap.values());
    }

    public ItemDtoWithBookingDates findItemWithBookingDatesAndCommentsByItemId(Long itemId) {
        Item item = getItemById(itemId);
        ItemDtoWithBookingDates itemDto = itemMapper.toItemDtoWithBookingDates(item, null, null,
                new ArrayList<>());

        Booking latestPastBooking = bookingRepository.findFirstByItem_IdAndEndBeforeAndStatusOrderByEndDesc(itemId,
                LocalDateTime.now().minusSeconds(5), BookingStatus.APPROVED);
        Booking earliestFutureBooking = bookingRepository.findFirstByItem_IdAndStartAfterAndStatusOrderByStart(itemId,
                LocalDateTime.now(), BookingStatus.APPROVED);

        itemDto.setNextBooking(earliestFutureBooking == null ? null : earliestFutureBooking.getStart());
        itemDto.setLastBooking(latestPastBooking == null ? null : latestPastBooking.getEnd());

        List<Comment> comments = commentRepository.findByItem_Id(itemId);

        itemDto.getComments().addAll(comments);

        return itemDto;
    }

    @Transactional
    public CommentOutDto createComment(Long owner, Long itemId, CommentDto commentDto) {
        List<Booking> bookingsCompletedByBooker = bookingRepository.findByBooker_IdAndEndBeforeAndStatusOrderByStartDesc(owner, LocalDateTime.now(),
                BookingStatus.APPROVED);
        Set<Long> itemsThatWereBookedByBooker = bookingsCompletedByBooker.stream()
                .map(booking -> booking.getItem().getId())
                .collect(Collectors.toSet());

        if (!itemsThatWereBookedByBooker.contains(itemId)) {
            log.warn("Пользователь с id = {} не бронировал вещи с id = {}", owner, itemId);
            throw new ValidationException("Пользователь с id = " + owner + " не бронировал вещи с id = " + itemId);
        }

        Comment comment = commentMapper.toComment(owner, getItemById(itemId), commentDto);

        Comment commentCreated = commentRepository.save(comment);

        log.info("Создан коммент с id = {} пользователем с userId = {}", commentCreated.getId(), owner);

        return commentMapper.toCommentOutDto(commentCreated, itemId, userService.getUserById(owner).getName());
    }
}
