package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage extends JpaRepository<Item, Long> {
    String FIND_ITEMS_BY_USER_ID_QUERY = "SELECT i " +
                                         "FROM Item i " +
                                         "WHERE i.owner = ?1";
    String FIND_ITEMS_BY_SEARCH_QUERY = "SELECT i " +
                                        "FROM Item i " +
                                        "WHERE (UPPER(i.name) LIKE UPPER(CONCAT('%', ?1, '%')) " +
                                           "OR UPPER(i.description) LIKE UPPER(CONCAT('%', ?1, '%'))) " +
                                          "AND i.available = true";

    @Query(FIND_ITEMS_BY_USER_ID_QUERY)
    List<Item> getItemsByUserId(Long userId);

    @Query(FIND_ITEMS_BY_SEARCH_QUERY)
    List<Item> searchItems(String text);
}
