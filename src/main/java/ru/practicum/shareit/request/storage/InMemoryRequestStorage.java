//package ru.practicum.shareit.request.storage;
//
//import org.springframework.stereotype.Repository;
//import ru.practicum.shareit.request.model.ItemRequest;
//import ru.practicum.shareit.user.model.User;
//import ru.practicum.shareit.user.storage.UserStorage;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Repository
//public class InMemoryRequestStorage implements RequestStorage {
//
//    private final UserStorage inMemoryUserStorage;
//    private final Map<Long, ItemRequest> requestMap;
//    private Long itemRequestId = 0L;
//
//    public InMemoryRequestStorage(UserStorage inMemoryUserStorage) {
//        this.inMemoryUserStorage = inMemoryUserStorage;
//        requestMap = new HashMap<>();
//    }
//
//    public ItemRequest createItemRequest(String description,
//                                         Long userId) {
//        Long id = getItemRequestId();
//        User userToAdd = inMemoryUserStorage.getUserById(userId);
//        ItemRequest itemRequest = new ItemRequest(id, description, userToAdd);
//        requestMap.put(id, itemRequest);
//
//        return itemRequest;
//    }
//
//    public Long getItemRequestId() {
//        return ++itemRequestId;
//    }
//
//    public ItemRequest getItemRequestById(Long id) {
//        return requestMap.get(id);
//    }
//}
